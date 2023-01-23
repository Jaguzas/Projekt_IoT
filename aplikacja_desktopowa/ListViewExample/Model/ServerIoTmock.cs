using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Globalization;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using System.Runtime.Remoting.Contexts;
using System.Diagnostics;

namespace ListViewExample.Model
{
    public class ServerIoTmock
    {
        private decimal[] signalValue = new decimal[9];
        Random rand = new Random();
        string IP;
        public class DataObject
        {
            public string unit { get; set; }
            public string name { get; set; }
            public decimal value { get; set; }
        }

        public decimal[] getTestSignal(int k)
        {

            return signalValue;
        }

        public void get_IP(string ip)
        {
            IP = ip;
        }
        public JArray getMeasurements()
        {
            string response;
            //   string url = "http://"+IP+"/czujniki.php";
            try
            {


                using (WebClient client = new WebClient())
                {
                    response = client.DownloadString("http://" + IP + "/czujniki.php");
                    DataObject[] data = JsonConvert.DeserializeObject<DataObject[]>(response);

                    decimal Value = data[0].value;
                    signalValue[0] = Value;


                    decimal Value2 = data[1].value;
                    signalValue[1] = Value2;


                    decimal Value3 = data[2].value;
                    signalValue[2] = Value3;

                    decimal Value4 = data[3].value;
                    signalValue[3] = Value4;


                    decimal Value5 = data[4].value;
                    signalValue[4] = Value5;


                    decimal Value6 = data[5].value;
                    signalValue[5] = Value6;

                    decimal Value7 = data[6].value;
                    signalValue[6] = Value7;

                    decimal Value8 = data[7].value;
                    signalValue[7] = Value8;

                    decimal Value9 = data[8].value;
                    signalValue[8] = Value9;
                }


                string jsonText = "[";

                jsonText += "{\"Name\":\"Temperature\",\"Data\":" + signalValue[0] + ",\"Unit\":\"C\"},";
                jsonText += "{\"Name\":\"Pressure\",\"Data\":" + signalValue[1] + ",\"Unit\":\"hPa\"},";
                jsonText += "{\"Name\":\"Humidity\",\"Data\":" + signalValue[2] + ",\"Unit\":\"%\"},";

                jsonText += "{\"Name\":\"Roll\",\"Data\":" + signalValue[3] + ",\"Unit\":\"Deg\"},";
                jsonText += "{\"Name\":\"Pitch\",\"Data\":" + signalValue[4] + ",\"Unit\":\"Deg\"},";
                jsonText += "{\"Name\":\"Yaw\",\"Data\":" + signalValue[5] + ",\"Unit\":\"Deg\"},";
                jsonText += "{\"Name\":\"x\",\"Data\":" + signalValue[6] + ",\"Unit\":\"\"},";
                jsonText += "{\"Name\":\"y\",\"Data\":" + signalValue[7] + ",\"Unit\":\"\"},";
                jsonText += "{\"Name\":\"m\",\"Data\":" + signalValue[8] + ",\"Unit\":\"\"}";

                jsonText += "]";

                return JArray.Parse(jsonText);
            }
            catch (Exception ex)
            {
                string jsonText = "[";

                jsonText += "{\"Name\":\"Temperature\",\"Data\":" + 0 + ",\"Unit\":\"C\"},";
                jsonText += "{\"Name\":\"Pressure\",\"Data\":" +0 + ",\"Unit\":\"hPa\"},";
                jsonText += "{\"Name\":\"Humidity\",\"Data\":" + 0 + ",\"Unit\":\"%\"},";

                jsonText += "{\"Name\":\"Roll\",\"Data\":" + 0 + ",\"Unit\":\"Deg\"},";
                jsonText += "{\"Name\":\"Pitch\",\"Data\":" + 0 + ",\"Unit\":\"Deg\"},";
                jsonText += "{\"Name\":\"Yaw\",\"Data\":" + 0 + ",\"Unit\":\"Deg\"},";
                jsonText += "{\"Name\":\"x\",\"Data\":" + 0 + ",\"Unit\":\"\"},";
                jsonText += "{\"Name\":\"y\",\"Data\":" + 0 + ",\"Unit\":\"\"},";
                jsonText += "{\"Name\":\"m\",\"Data\":" + 0 + ",\"Unit\":\"\"}";
                //     jsonText += "{\"Name\":\"m\",\"Data\":" + "\"error\"" + ",\"Unit\":\"\"}";

                jsonText += "]";
                Debug.WriteLine("NETWORK ERROR");
                Debug.WriteLine(ex);
                return JArray.Parse(jsonText);
            }

        }
    }
}
