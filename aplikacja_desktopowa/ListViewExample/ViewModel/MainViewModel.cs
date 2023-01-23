using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using ListViewExample.Model;
using System;
using System.Runtime.CompilerServices;
using System.Windows.Media;
using System.Windows;
using Newtonsoft.Json.Linq;
using OxyPlot;
using OxyPlot.Axes;
using OxyPlot.Series;
using OxyPlot.WindowsForms;
using ListViewExample.View;



namespace ListViewExample.ViewModel
{
    using Model;
    using static System.Windows.Forms.VisualStyles.VisualStyleElement.Window;
    
    public class MainViewModel : INotifyPropertyChanged
    {
        public static string IP;
        #region Fields


        private readonly Action<string, Color> setColorHandler;

        private LedDisplay ledDisplay;  //!< LED display model
        private IoTServer server;       //!< IoT server model

        #endregion Fileds

        #region Properties

        public int DisplaySizeX { get => ledDisplay.SizeX; }
        public int DisplaySizeY { get => ledDisplay.SizeY; }
        public Color DisplayOffColor { get => ledDisplay.OffColor; }

        public ButtonCommandWithParameter CommonButtonCommand { get; set; }
        public ButtonCommand SendRequestCommand { get; set; }
        public ButtonCommand SendClearCommand { get; set; }
        public ButtonCommand changeSTT { get; set; }
        private byte _r;
        public int R
        {
            get
            {
                return _r;
            }
            set
            {
                if (_r != (byte)value)
                {
                    _r = (byte)value;
                    ledDisplay.ActiveColorR = _r;
                    SelectedColor = new SolidColorBrush(ledDisplay.ActiveColor);
                    OnPropertyChanged("R");
                }
            }
        }

        private byte _g;
        public int G
        {
            get
            {
                return _g;
            }
            set
            {
                if (_g != (byte)value)
                {
                    _g = (byte)value;
                    ledDisplay.ActiveColorG = _g;
                    SelectedColor = new SolidColorBrush(ledDisplay.ActiveColor);
                    OnPropertyChanged("G");
                }
            }
        }

        private byte _b;
        public int B
        {
            get
            {
                return _b;
            }
            set
            {
                if (_b != (byte)value)
                {
                    _b = (byte)value;
                    ledDisplay.ActiveColorB = _b;
                    SelectedColor = new SolidColorBrush(ledDisplay.ActiveColor);
                    OnPropertyChanged("B");
                }
            }
        }

        private SolidColorBrush _selectedColor;
        public SolidColorBrush SelectedColor
        {
            get
            {
                return _selectedColor;
            }
            set
            {
                if (_selectedColor != value)
                {
                    _selectedColor = value;
                    OnPropertyChanged("SelectedColor");
                }
            }
        }

        #endregion Properties
        public ObservableCollection<MeasurementViewModel> Measurements { get;  set; }
        public ButtonCommand2 Refresh { get; set; }

        private ServerIoTmock ServerMock = new ServerIoTmock();

        public PlotModel chart { get; set; } //!< OxyPlot ViewModel
        public ButtonCommand RunButton { get; set; }

        private MultimediaTimer filterTimer;

        private double samplesMax;

        private int k = 0; //!< Samples counter

        private bool signalMock = true;


        private ServerIoT server2;
        
        private double sampleTime = Double.Parse(File1.Sampletime);
        public bool SignalMock
        {
            get
            {
                return signalMock;
            }
            set
            {
                if (signalMock != value)
                {
                    signalMock = value;
                    OnPropertyChanged("SignalLocal");
                }
            }
        }
        public string SampleFreq
        {
            get
            {
                
                return (1.0 / sampleTime).ToString();
            }
            set
            {
                
                if (double.TryParse(value, out double sf))
                {
                    if (sampleTime != (1.0 / sf))
                    {
                        sampleTime = (1.0 / sf);
                        OnPropertyChanged("SampleFreq");
                    }
                }
            }
        }
        public MainViewModel(Action<String, Color> handler)
        {
            // Create new collection for measurements data
            Measurements = new ObservableCollection<MeasurementViewModel>();

            // Bind button with action
            Refresh = new ButtonCommand2(RefreshHandler);

            ledDisplay = new LedDisplay(0x00000000);
            SelectedColor = new SolidColorBrush(ledDisplay.ActiveColor);

            setColorHandler = handler;
            CommonButtonCommand = new ButtonCommandWithParameter(SetButtonColor);
            SendRequestCommand = new ButtonCommand(SendControlRequest);
            SendClearCommand = new ButtonCommand(ClearDisplay);
            server = new IoTServer("");
            
            ChartInit();
            RunButton = new ButtonCommand(RunDemo);
            MyFirData.sampletime = Double.Parse(File1.Sampletime);
           // sampleTime = Double.Parse(File1.Sampletime); ;
            
            // samplesMax = chart.Axes[0].Maximum / sampleTime;
            int samplesMax = 10;

            server2 = new ServerIoT("");
            
        }
       


    //    public PlotModel chart { get; set; } //!< OxyPlot ViewModel
    //    public ButtonCommand RunButton { get; set; }

  

 


        void RefreshHandler()
        {
            
            ServerMock.get_IP(File1.IP);
            // Read data from server in JSON array format
            // TODO: replace mock with network comunnication
            JArray measurementsJsonArray = ServerMock.getMeasurements();

            // Convert generic JSON array container to list of specific type
            var measurementsList = measurementsJsonArray.ToObject<List<MeasurementModel>>();

            // Add new elements to collection
            if(Measurements.Count < measurementsList.Count)
            {
                foreach (var m in measurementsList)
                    Measurements.Add(new MeasurementViewModel(m));
            }
            // Update existing elements in collection
            else
            {
                for (int i = 0; i < Measurements.Count; i++)
                    Measurements[i].UpdateWithModel(measurementsList[i]);
            }

        }
        private async void FilterProcedure(object sender, EventArgs e)
        {
            sampleTime = Double.Parse(File1.Sampletime);
            server2.get_IP(File1.IP);
            decimal[] tmp;
            tmp = server2.getTestSignal(k);
            double yaw = (double)(decimal)tmp[0];
            double pitch = (double)(decimal)tmp[1];
            double roll = (double)(decimal)tmp[2];
            // display data (OxyPlot)

            //  if (k <= 50) { 
            (chart.Series[0] as LineSeries).Points.Add(new DataPoint(k * sampleTime, yaw));
            (chart.Series[1] as LineSeries).Points.Add(new DataPoint(k * sampleTime, pitch));
            (chart.Series[2] as LineSeries).Points.Add(new DataPoint(k * sampleTime, roll));
 
            chart.InvalidatePlot(true);
     
            k++;
        }
        //wykresy
        private void RunDemo()
        {
            server2.get_IP(File1.IP);
            if (filterTimer == null)
            {
                k = 0;

                (chart.Series[0] as LineSeries).Points.Clear();
                (chart.Series[1] as LineSeries).Points.Clear();
                (chart.Series[2] as LineSeries).Points.Clear();

                filterTimer = new MultimediaTimer(10 * sampleTime);
                filterTimer.Elapsed += new EventHandler(FilterProcedure);
                filterTimer.Start();
            }
        }
        private void ChartInit()
        {
            chart = new PlotModel { Title = "Orientation" };

            chart.Axes.Add(new LinearAxis()
            {
                Position = AxisPosition.Bottom,
                //    Minimum = 0,
                //   Maximum = 50,
                Key = "Horizontal",
                Unit = "sec",
                Title = "Time",
                //    MajorStep = 1,
                MajorGridlineColor = OxyColor.Parse("#FFD3D3D3"),
                MajorGridlineStyle = LineStyle.Solid,
                //    MajorGridlineThickness = 1

            }); ;
            chart.Axes.Add(new LinearAxis()
            {
                Position = AxisPosition.Left,
                //   Minimum = 0,
                //    Maximum = 360,
                Key = "Vertical",
                Unit = "-",
                Title = "Amplitude",
                MajorStep = 15,
                MajorGridlineColor = OxyColor.Parse("#FFD3D3D3"),
                MajorGridlineStyle = LineStyle.Solid,
                //    MajorGridlineThickness = 1
            });

            chart.Series.Add(new LineSeries()
            {

                Title = "yaw",
                Color = OxyColor.Parse("#FF0000FF"),

            });
            chart.Series.Add(new LineSeries()
            {
                Title = "pitch",
                Color = OxyColor.Parse("#FF00FF00")
            });
            chart.Series.Add(new LineSeries()
            {
                Title = "roll",
                Color = OxyColor.Parse("#FF000000")
            });
        }


     
            
        
        #region Methods

        /**
         * @brief Conversion method: LED indicator Name to LED x-y position
         * @param name LED indicator Button Name propertie 
         * @return Tuple with LED x-y position (0=x, 1=y)
         */
        public (int, int) LedTagToIndex(string name)
        {
            return (int.Parse(name.Substring(3, 1)), int.Parse(name.Substring(4, 1)));
        }

        /**
         * @brief Conversion method: LED x-y position to LED indicator Name
         * @param x LED horizontal position in display
         * @param y LED vertical position in display
         * @return LED indicator Button Name property
         */
        public string LedIndexToTag(int i, int j)
        {
            return "LED" + i.ToString() + j.ToString();
        }

        /**
         * @brief LED indicator Click event handling procedure
         * @param parameter LED indicator Button Name property
         */
        private void SetButtonColor(string parameter)
        {
            // Set active color as background
            setColorHandler(parameter, SelectedColor.Color);
            // Find element x-y position
            (int x, int y) = LedTagToIndex(parameter);
            // Update LED display data model
            ledDisplay.UpdateModel(x, y);
        }

        /**
         * @brief Clear button Click event handling procedure
         */
        private async void ClearDisplay()
        {
            server.get_IP(File1.IP);
            // Clear LED display GUI
            for (int i = 0; i < ledDisplay.SizeX; i++)
                for (int j = 0; j < ledDisplay.SizeY; j++)
                    setColorHandler(LedIndexToTag(i, j), ledDisplay.OffColor);

            // Clear LED display data model
            ledDisplay.ClearModel();

            // Clear physical LED display
           await server.PostControlRequest(ledDisplay.getClearPostData());
        }

        /**
         * @brief Send button Click event handling procedure
         */
        private async void SendControlRequest()
        {
            server.get_IP(File1.IP);
            await server.PostControlRequest(ledDisplay.getControlPostData());
        }

        #endregion Methods
        #region PropertyChanged

        public event PropertyChangedEventHandler PropertyChanged;

        /**
         * @brief Simple function to trigger event handler
         * @params propertyName Name of ViewModel property as string
         */
        protected void OnPropertyChanged(string propertyName)
        {
            PropertyChangedEventHandler handler = PropertyChanged;
            if (handler != null) handler(this, new PropertyChangedEventArgs(propertyName));
        }

        protected bool SetProperty<T>(ref T field, T newValue, [CallerMemberName] string propertyName = null)
        {
            if (!Equals(field, newValue))
            {
                field = newValue;
                PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
                return true;
            }

            return false;
        }

        private PlotModel myModel;

        public PlotModel MyModel { get => myModel; set => SetProperty(ref myModel, value); }

        #endregion
    }
}
