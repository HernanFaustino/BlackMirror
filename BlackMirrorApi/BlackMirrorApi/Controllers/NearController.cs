using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using FireSharp;
using FireSharp.Config;
using FireSharp.Interfaces;
using FireSharp.Response;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace BlackMirrorApi.Controllers
{
    [Produces("application/json")]
    [Route("api/Near")]
    public class NearController : Controller
    {
        IFirebaseClient client;

        public NearController()
        {
            IFirebaseConfig config = new FirebaseConfig
            {
                AuthSecret = "xAHhzYXgJnoTX0RMMp3pnIQtFupbHhCqldI6lWIn",
                BasePath = "https://blackmirrordb.firebaseio.com/"
            };

            client = new FirebaseClient(config);
        }

        // GET api/near
        [HttpGet("{id}")]
        public async Task<IDictionary<string, User>> GetObjectsAsync(String id)
        {
            FirebaseResponse response = await client.GetAsync("user/" + id);
            User myuser = response.ResultAs<User>();

            FirebaseResponse response2 = await client.GetAsync("user/");
            IDictionary<string, User> users = response2.ResultAs<IDictionary<string, User>>();
            IEnumerable<User> list = users.Values;
            IDictionary<string, User> nearUsers = new Dictionary<string, User>();
            foreach (var usr in users)
            {
                if (DistanceTo(myuser.LastLocation, usr.Value.LastLocation) <= 200 && id != usr.Key)
                {
                    nearUsers.Add(usr);
                }
            }
            return nearUsers;
        }

        // PUT api/near/#
        [HttpPut("{id}")]
        public async Task<User> PutAsync(String id, [FromBody]RateObj rate)
        {

            FirebaseResponse response = await client.GetAsync("user/" + id);
            User user = response.ResultAs<User>();

            user.Rating = Math.Round((user.Rating * user.NRates + rate.Rate) / (user.NRates + 1.0), 1);
            user.NRates = user.NRates + 1;

            FirebaseResponse response2 = await client.UpdateAsync("user/" + id, user);
            return response2.ResultAs<User>();
        }

        public static double DistanceTo(Location loc1, Location loc2)
        {
            double lat1 = loc1.Lat;
            double lon1 = loc1.Long;
            double lat2 = loc2.Lat;
            double lon2 = loc2.Long;

            double rlat1 = Math.PI * lat1 / 180;
            double rlat2 = Math.PI * lat2 / 180;
            double theta = lon1 - lon2;
            double rtheta = Math.PI * theta / 180;
            double dist =
                Math.Sin(rlat1) * Math.Sin(rlat2) + Math.Cos(rlat1) *
                Math.Cos(rlat2) * Math.Cos(rtheta);
            dist = Math.Acos(dist);
            dist = dist * 180 / Math.PI;
            dist = dist * 60 * 1.1515;

            return dist * 1.609344 * 1000;
        }
    }

    public class RateObj {
        public int Rate { get; set; }
    }
}