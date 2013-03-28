(function() {
  var Person, city, close, contents, forecast, futurists, name, open, street, tag, temp, theBait, theSwitch, weatherReport, _i, _ref, _ref1, _ref2, _ref3, _ref4,
    __slice = [].slice;

  theBait = 1000;

  theSwitch = 0;

  _ref = [theSwitch, theBait], theBait = _ref[0], theSwitch = _ref[1];

  weatherReport = function(location) {
    return [location, 72, "Mostly Sunny"];
  };

  _ref1 = weatherReport("Berkeley, CA"), city = _ref1[0], temp = _ref1[1], forecast = _ref1[2];

  futurists = {
    sculptor: "Umberto Boccioni",
    painter: "Vladimir Burliuk",
    poet: {
      name: "F.T. Marinetti",
      address: ["Via Roma 42R", "Bellagio, Italy 22021"]
    }
  };

  _ref2 = futurists.poet, name = _ref2.name, (_ref3 = _ref2.address, street = _ref3[0], city = _ref3[1]);

  tag = "<impossible>";

  _ref4 = tag.split(""), open = _ref4[0], contents = 3 <= _ref4.length ? __slice.call(_ref4, 1, _i = _ref4.length - 1) : (_i = 1, []), close = _ref4[_i++];

  Person = (function() {
    function Person(options) {
      this.name = options.name, this.age = options.age, this.height = options.height;
    }

    return Person;

  })();

}).call(this);
