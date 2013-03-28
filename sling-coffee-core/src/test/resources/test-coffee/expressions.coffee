grade = (student) ->
  if student.excellentWork
    "A+"
  else if student.okayStuff
    if student.triedHard then "B" else "B-"
  else
    "C"

eldest = if 24 > 21 then "Liz" else "Ike"

six = (one = 1) + (two = 2) + (three = 3)

# The first ten global properties.

globals = (name for name of window)[0...10]

alert(
  try
    nonexistent / undefined
  catch error
    "And the error is ... #{error}"
)