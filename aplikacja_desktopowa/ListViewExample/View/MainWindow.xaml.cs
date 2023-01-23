using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.ComponentModel;
    using ListViewExample.Model;
using OxyPlot.WindowsForms;
using OxyPlot;
using OxyPlot.Series;



namespace ListViewExample.View
{
    using OxyPlot.Series;
    using OxyPlot.Wpf;
    using ViewModel;
    public class File1
    {
        public static string IP;
        public static string Sampletime;

    }
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        private readonly MainViewModel viewmodel;
        public PlotModel MyModel { get; set; }
        public ServerIoT ServerIoT { get; private set; }

        public MainWindow()
        {
            InitializeComponent();

            this.ResizeMode = ResizeMode.NoResize;
            
            File1.Sampletime = box2.Text;
            File1.IP = box1.Text;
            double tmp = 1 / double.Parse(File1.Sampletime);
            lab2.Content = tmp;
            MyModel = new PlotModel { Title = "My Plot" };

            LineSeries mySeries = new LineSeries
            {
                Title = "My Data",
                MarkerType = MarkerType.Circle
            };

            mySeries.Points.Add(new DataPoint(0, 4));
            mySeries.Points.Add(new DataPoint(10, 13));
            mySeries.Points.Add(new DataPoint(20, 15));
            MyModel.Series.Add(mySeries);
            this.DataContext = this;
            //DataContext = new MainViewModel();
            //InitializeComponent();

            viewmodel = new MainViewModel(SetButtonColor);
            DataContext = viewmodel;

            /* Button matrix grid definition */
            for (int i = 0; i < viewmodel.DisplaySizeX; i++)
            {
                ButtonMatrixGrid.ColumnDefinitions.Add(new ColumnDefinition());
                ButtonMatrixGrid.ColumnDefinitions[i].Width = new GridLength(1, GridUnitType.Star);
            }

            for (int i = 0; i < viewmodel.DisplaySizeY; i++)
            {
                ButtonMatrixGrid.RowDefinitions.Add(new RowDefinition());
                ButtonMatrixGrid.RowDefinitions[i].Height = new GridLength(1, GridUnitType.Star);
            }

            for (int i = 0; i < viewmodel.DisplaySizeX; i++)
            {
                for (int j = 0; j < viewmodel.DisplaySizeY; j++)
                {
                    // <Button
                    Button led = new Button()
                    {
                        // Name = "LEDij"
                        Name = viewmodel.LedIndexToTag(i, j),
                        // CommandParameter = "LEDij"
                        CommandParameter = viewmodel.LedIndexToTag(i, j),
                        // Style="{StaticResource LedButtonStyle}"
                        Style = (Style)FindResource("LedButtonStyle"),
                        // Bacground="{StaticResource ... }"
                        Background = new SolidColorBrush(viewmodel.DisplayOffColor),
                        // BorderThicness="2"
                        BorderThickness = new Thickness(2),
                    };
                    // Command="{Binding CommonButtonCommand}" 
                    led.SetBinding(Button.CommandProperty, new Binding("CommonButtonCommand"));
                    // Grid.Column="i" 
                    Grid.SetColumn(led, i);
                    // Grid.Row="j"
                    Grid.SetRow(led, j);
                    // />

                    ButtonMatrixGrid.Children.Add(led);
                    ButtonMatrixGrid.RegisterName(led.Name, led);
                }
            }

        }



        private void Button_Click(object sender, RoutedEventArgs e)
        {

        }
        private void SetButtonColor(string name, Color color)
        {
            (ButtonMatrixGrid.FindName(name) as Button).Background = new SolidColorBrush(color);
        }

       
        private void TextBox_TextChanged(object sender, TextChangedEventArgs e)
            {
            
            }

        private void Button_Click_1(object sender, RoutedEventArgs e)
        {
            File1.IP = (box1.Text).ToString();
           // lab1.Content = box1.Text;
        }

        private void TextBox_TextChanged_1(object sender, TextChangedEventArgs e)
        {
            
        }

        private void Button_Click_2(object sender, RoutedEventArgs e)
        {
            File1.Sampletime = (box2.Text);
            MyFirData.sampletime = Double.Parse(box2.Text);
          //  lab1.Content = MyFirData.sampletime;
            double tmp = 1 / double.Parse(File1.Sampletime);
            lab2.Content = tmp;
        }
    }
}
