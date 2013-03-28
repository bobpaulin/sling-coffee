(function() {
  var courses, dish, filename, food, foods, i, lyrics, num, _fn, _i, _j, _k, _l, _len, _len1, _len2, _len3, _ref;

  _ref = ['toast', 'cheese', 'wine'];
  for (_i = 0, _len = _ref.length; _i < _len; _i++) {
    food = _ref[_i];
    eat(food);
  }

  courses = ['greens', 'caviar', 'truffles', 'roast', 'cake'];

  for (i = _j = 0, _len1 = courses.length; _j < _len1; i = ++_j) {
    dish = courses[i];
    menu(i + 1, dish);
  }

  foods = ['broccoli', 'spinach', 'chocolate'];

  for (_k = 0, _len2 = foods.length; _k < _len2; _k++) {
    food = foods[_k];
    if (food !== 'chocolate') {
      eat(food);
    }
  }

  if (this.studyingEconomics) {
    while (supply > demand) {
      buy();
    }
    while (!(supply > demand)) {
      sell();
    }
  }

  num = 6;

  lyrics = (function() {
    var _results;

    _results = [];
    while (num -= 1) {
      _results.push("" + num + " little monkeys, jumping on the bed.    One fell out and bumped his head.");
    }
    return _results;
  })();

  _fn = function(filename) {
    return fs.readFile(filename, function(err, contents) {
      return compile(filename, contents.toString());
    });
  };
  for (_l = 0, _len3 = list.length; _l < _len3; _l++) {
    filename = list[_l];
    _fn(filename);
  }

}).call(this);
