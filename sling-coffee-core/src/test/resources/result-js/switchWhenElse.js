(function() {
  var grade, score;

  switch (day) {
    case "Mon":
      go(work);
      break;
    case "Tue":
      go(relax);
      break;
    case "Thu":
      go(iceFishing);
      break;
    case "Fri":
    case "Sat":
      if (day === bingoDay) {
        go(bingo);
        go(dancing);
      }
      break;
    case "Sun":
      go(church);
      break;
    default:
      go(work);
  }

  score = 76;

  grade = (function() {
    switch (false) {
      case !(score < 60):
        return 'F';
      case !(score < 70):
        return 'D';
      case !(score < 80):
        return 'C';
      case !(score < 90):
        return 'B';
      default:
        return 'A';
    }
  })();

}).call(this);
