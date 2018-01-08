using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using FireSharp;
using FireSharp.Config;
using FireSharp.Interfaces;
using FireSharp.Response;
using Microsoft.AspNetCore.Mvc;

namespace BlackMirrorApi.Controllers
{
    [Route("api/[controller]")]
    public class ValuesController : Controller
    {
        IFirebaseClient client;

        public ValuesController()
        {
            IFirebaseConfig config = new FirebaseConfig
            {
                AuthSecret = "xAHhzYXgJnoTX0RMMp3pnIQtFupbHhCqldI6lWIn",
                BasePath = "https://blackmirrordb.firebaseio.com/"
            };

            client = new FirebaseClient(config);
        }

        // GET api/values
        [HttpGet]
        public async Task<IDictionary<string, User>> GetObjectsAsync()
        {
            FirebaseResponse response = await client.GetAsync("user/");
            IDictionary<string, User> users = response.ResultAs<IDictionary<string, User>>();
            return users;
        }

        // GET api/values/5
        [HttpGet("{id}")]
        public async Task<User> GetAsync(string id)
        {
            FirebaseResponse response = await client.GetAsync("user/" + id);
            return response.ResultAs<User>();
        }

        // POST api/values
        [HttpPost]
        public async Task<Key> PostAsync([FromBody]User user)
        {
            PushResponse response = await client.PushAsync("user/", user);
            return new Key(response.Result.name);
        }

        // PUT api/values/5
        [HttpPut("{id}")]
        public async Task<User> PutAsync(String id, [FromBody]User user)
        {
            FirebaseResponse response = await client.GetAsync("user/" + id);
            User usr = response.ResultAs<User>();

            if (user.Name == null) user.Name = usr.Name;
            if (user.Image == null) user.Image = usr.Image;
            if (user.LastLocation == null) user.LastLocation = usr.LastLocation;
            if (user.NRates == 0) user.NRates = usr.NRates;
            if (user.Rating == 0.0) user.Rating = usr.Rating;

            FirebaseResponse response2 = await client.UpdateAsync("user/" + id, user);
            return response2.ResultAs<User>();
        }

        // DELETE api/values/5
        [HttpDelete("{id}")]
        public void Delete(int id)
        {
        }
    }

    public class Key {
        public Key(string id)
        {
            Id = id;
        }
        public string Id { get; set; }
    }
}
