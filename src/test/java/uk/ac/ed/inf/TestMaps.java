package uk.ac.ed.inf;

public final class TestMaps {

    //simple restaurant directly west of Appleton
    public static final String RESTAURANTS_WEST = """
    [
      {
        "name": "Testaurant",
        "location": { "lng": -3.19, "lat": 55.944494},
        "menu": [
          { "name": "R2: Meat Lover", "priceInPence": 1400 }
        ],
        "openingDays": [
          "MONDAY",
          "TUESDAY",
          "WEDNESDAY",
          "THURSDAY",
          "FRIDAY",
          "SATURDAY",
          "SUNDAY"
        ]
      }
    ]
    """;

    public static final String RESTAURANTS_SOUTHER = """
    [
      {
        "name": "Testaurant",
        "location": { "lng": -3.19, "lat": 55.94435},
        "menu": [
          { "name": "R2: Meat Lover", "priceInPence": 1400 }
        ],
        "openingDays": [
          "MONDAY",
          "TUESDAY",
          "WEDNESDAY",
          "THURSDAY",
          "FRIDAY",
          "SATURDAY",
          "SUNDAY"
        ]
      }
    ]
    """;

    public static final String RESTAURANTS_OUTSIDE_CENTRAL = """
    [
      {
        "name": "Testaurant",
        "location": { "lng": -3.1943, "lat": 55.9459},
        "menu": [
          { "name": "R2: Meat Lover", "priceInPence": 1400 }
        ],
        "openingDays": [
          "MONDAY",
          "TUESDAY",
          "WEDNESDAY",
          "THURSDAY",
          "FRIDAY",
          "SATURDAY",
          "SUNDAY"
        ]
      }
    ]
    """;

    public static final String RESTAURANTS_FAR = """
    [
      {
        "name": "Testaurant",
        "location": { "lng": -3.154058, "lat": 55.978268},
        "menu": [
          { "name": "R2: Meat Lover", "priceInPence": 1400 }
        ],
        "openingDays": [
          "MONDAY",
          "TUESDAY",
          "WEDNESDAY",
          "THURSDAY",
          "FRIDAY",
          "SATURDAY",
          "SUNDAY"
        ]
      }
    ]
    """;

    public static final String RESTAURANTS_EXTREME = """
    [
      {
        "name": "Testaurant",
        "location": { "lng": 0.0, "lat": 0.0},
        "menu": [
          { "name": "R2: Meat Lover", "priceInPence": 1400 }
        ],
        "openingDays": [
          "MONDAY",
          "TUESDAY",
          "WEDNESDAY",
          "THURSDAY",
          "FRIDAY",
          "SATURDAY",
          "SUNDAY"
        ]
      }
    ]
    """;

    //central area around Appleton Tower
    public static final String CENTRAL = """
    {
      "name": "central",
      "vertices": [
        {
          "lng": -3.192473,
          "lat": 55.946233
        },
        {
          "lng": -3.192473,
          "lat": 55.942617
        },
        {
          "lng": -3.184319,
          "lat": 55.942617
        },
        {
          "lng": -3.184319,
          "lat": 55.946233
        },
        {
          "lng": -3.192473,
          "lat": 55.946233
        }
      ]
    }
    """;

    //small square nfz blocking the straight path
    public static final String NO_FLY_SIMPLE = """
    [
      {
        "name": "No fly",
        "vertices": [
          { "lng": -3.1892, "lat": 55.9446 },
          { "lng": -3.1885, "lat": 55.9446 },
          { "lng": -3.1885, "lat": 55.9443 },
          { "lng": -3.1892, "lat": 55.9443 },
          { "lng": -3.1892, "lat": 55.9446 }
        ]
      }
    ]
    """;

    //nfz around appleton
    public static final String NO_FLY_IMPOSSIBLE_GOAL = """
    [
      {
        "name": "No fly",
        "vertices": [
          { "lng": -3.1875, "lat": 55.9447 },
          { "lng": -3.1875, "lat": 55.9442 },
          { "lng": -3.1862, "lat": 55.9442 },
          { "lng": -3.1862, "lat": 55.9447 },
          { "lng": -3.1875, "lat": 55.9447 }
        ]
      }
    ]
    """;

    //nfz around restaurant
    public static final String NO_FLY_IMPOSSIBLE_START = """
    [
      {
        "name": "No fly",
        "vertices": [
          { "lng": -3.1898, "lat": 55.9446 },
          { "lng": -3.1898, "lat": 55.9443 },
          { "lng": -3.1902, "lat": 55.9443 },
          { "lng": -3.1902, "lat": 55.9446 },
          { "lng": -3.1898, "lat": 55.9446 }
        ]
      }
    ]
    """;

    //long nfz between restaurant and appleton
    public static final String NO_FLY_WALL = """
    [
      {
        "name": "No fly",
        "vertices": [
          { "lng": -3.1891, "lat": 55.9453 },
          { "lng": -3.1891, "lat": 55.9437 },
          { "lng": -3.1889, "lat": 55.9437 },
          { "lng": -3.1889, "lat": 55.9453 },
          { "lng": -3.1891, "lat": 55.9453 }
        ]
      }
    ]
    """;

    //two close nfzs around optimal path
    public static final String NO_FLY_CORRIDOR = """
    [
     {
      "name":"A",
      "vertices":[
       {"lng":-3.1890,"lat":55.9445},
       {"lng":-3.1884,"lat":55.9445},
       {"lng":-3.1884,"lat":55.9447},
       {"lng":-3.1890,"lat":55.9447},
       {"lng":-3.1890,"lat":55.9445}
      ]
     },
     {
      "name":"B",
      "vertices":[
       {"lng":-3.1890,"lat":55.9445},
       {"lng":-3.1890,"lat":55.9436},
       {"lng":-3.1884,"lat":55.9444},
       {"lng":-3.1884,"lat":55.9444},
       {"lng":-3.1890,"lat":55.9445}
      ]
     }
    ]
    """;
    public static final String EMPTY_LIST = "[]";
}
