theBait   = 1000
theSwitch = 0

[theBait, theSwitch] = [theSwitch, theBait]

weatherReport = (location) ->
  # Make an Ajax request to fetch the weather...
  [location, 72, "Mostly Sunny"]

[city, temp, forecast] = weatherReport "Berkeley, CA"

futurists =
  sculptor: "Umberto Boccioni"
  painter:  "Vladimir Burliuk"
  poet:
    name:   "F.T. Marinetti"
    address: [
      "Via Roma 42R"
      "Bellagio, Italy 22021"
    ]

{poet: {name, address: [street, city]}} = futurists

tag = "<impossible>"

[open, contents..., close] = tag.split("")

class Person
  constructor: (options) -> 
    {@name, @age, @height} = options