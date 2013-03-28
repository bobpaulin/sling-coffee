(function() {
  var eldest, error, globals, grade, name, one, six, three, two;

  grade = function(student) {
    if (student.excellentWork) {
      return "A+";
    } else if (student.okayStuff) {
      if (student.triedHard) {
        return "B";
      } else {
        return "B-";
      }
    } else {
      return "C";
    }
  };

  eldest = 24 > 21 ? "Liz" : "Ike";

  six = (one = 1) + (two = 2) + (three = 3);

  globals = ((function() {
    var _results;

    _results = [];
    for (name in window) {
      _results.push(name);
    }
    return _results;
  })()).slice(0, 10);

  alert((function() {
    try {
      return nonexistent / void 0;
    } catch (_error) {
      error = _error;
      return "And the error is ... " + error;
    }
  })());

}).call(this);
