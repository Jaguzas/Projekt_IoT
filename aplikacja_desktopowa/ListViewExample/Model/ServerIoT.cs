using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace ListViewExample.Model
{
    public class DataObject
    {
        public string unit { get; set; }
        public string name { get; set; }
        public decimal value { get; set; }
    }

    public class ServerIoT
    {
        private string protocol = "http://";
        public string ip;
        private string script1 = "czujniki.php"; // cgi-bin/server/test_signal.py
   //     private string script2 = "led_display.php";
        private decimal[] signalValue = new decimal[3];

        public ServerIoT(string _ip)
        {
            ip = _ip + "/";
        }
        public void get_IP(string IPP)
        {
            ip = IPP;
        }
        public decimal[] getTestSignal(int k)
        {
            string response;
            //string url = "http://192.168.1.134/czujniki.php";
            string url = "http://" +ip  + "/czujniki.php";
            try
            {
                using (WebClient client = new WebClient())
                {
                    response = client.DownloadString("http://" + ip + "/czujniki.php");
                    DataObject[] data = JsonConvert.DeserializeObject<DataObject[]>(response);

                    decimal Value = data[3].value;
                    signalValue[0] = Value;


                    decimal Value2 = data[4].value;
                    signalValue[1] = Value2;


                    decimal Value3 = data[5].value;
                    signalValue[2] = Value3;

                }
                return signalValue ;
            }
            catch(Exception ex)
            {
                Debug.WriteLine("NETWORK ERROR");
                Debug.WriteLine(ex);
                return signalValue;
            }
            
        }
    }
}
