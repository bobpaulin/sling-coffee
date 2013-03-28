(function() {
  var age, ages, child, countdown, num, yearsOld;

  countdown = (function() {
    var _i, _results;

    _results = [];
    for (num = _i = 10; _i >= 1; num = --_i) {
      _results.push(num);
    }
    return _results;
  })();

  yearsOld = {
    max: 10,
    ida: 9,
    tim: 11
  };

  ages = (function() {
    var _results;

    _results = [];
    for (child in yearsOld) {
      age = yearsOld[child];
      _results.push("" + child + " is " + age);
    }
    return _results;
  })();

}).call(this);
