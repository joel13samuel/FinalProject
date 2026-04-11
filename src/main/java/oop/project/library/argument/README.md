# Argument System

Handles parsing a single String input value into typed data.

## Development Notes

- The main idea behind my argument system is `ArgumentType<T>`. I used that as a simple way to represent "take in one `String` and turn it into one typed value."
- Instead of putting everything into one big helper class, I split the basic parsers into smaller classes. That made the code easier to read and easier to debug while I was still figuring out the design.
- I added basic argument types for `Integer`, `Double`, `String`, `Boolean`, and a generic `CustomArgumentType<T>` so I could support things like `LocalDate` without writing a brand new parser every time.
- For validation, I made separate argument types for the cases I needed in the PoC:
  - `RangedIntegerArgumentType`
  - `RangedDoubleArgumentType`
  - `ChoiceStringArgumentType`
- This let me move some of the validation logic out of the scenario methods and into the Argument system itself, which feels closer to the goal of the project.
- One weakness of the current design is that validation uses separate classes for each kind of rule. That was easier to build and understand, but it could get repetitive if I add more validation types later.
- Another weakness is that some exception handling still happens in the scenarios so the provided tests see the errors as coming from the scenario methods.

## PoC Design Analysis

### Individual Review (Argument Lead)

- One design choice that worked well was using `ArgumentType<T>` as the base abstraction. It gave all of the argument parsers the same shape, which made the system feel more consistent.
- Another good decision was adding `CustomArgumentType<T>`. That gave us a simple way to support custom parsing like `LocalDate` without hardcoding every possible type into the system.
- One weaker part of the design is how validation works right now. Having separate classes like `RangedIntegerArgumentType` and `ChoiceStringArgumentType` made things easier to implement, but it is not the most flexible design long-term.
- Another weaker part is that some validation and exception translation still happens in the scenario layer. It works, but ideally more of that behavior would live fully inside the Argument system.

### Individual Review (Command Lead)

- One thing that looks promising is that the Command side can build on top of `ArgumentType<T>` instead of doing all parsing itself. That should reduce repeated logic later.

- Another good part is that positional and named arguments can stay a Command-system problem, while the Argument system stays focused on parsing one value at a time.

- One thing that still feels unclear is exactly how the Command system will store and use typed arguments internally. The Argument system is in place, but the connection between the two systems could be more clearly defined.

- Another concern is that because we built the PoC scenario-first, there is still some repeated parsing logic in the scenarios that a fuller Command API would probably want to clean up later.


### Team Review

- I think the current design is good enough for the PoC, but there are still open questions. The biggest one is whether validation should keep using dedicated classes or whether it should move to a more general wrapper-based design later.
- I also want to avoid the Command system duplicating responsibilities that really belong in the Argument system, especially when it comes to parsing and validation.
- Going forward, one of the harder design problems will be making the command API easy for users to write while still keeping the type safety I want from the Argument layer.
