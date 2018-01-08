using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace BlackMirrorApi.Controllers
{
    public class User
    {
        public string Name { get; set; }
        public string Image { get; set; }
        public double Rating { get; set; }
        public int NRates { get; set; }
        public Location LastLocation { get; set; }
    }

    public class Location
    {
        public double Lat { get; set; }
        public double Long { get; set; }
        public double Alt { get; set; }
    }
}
