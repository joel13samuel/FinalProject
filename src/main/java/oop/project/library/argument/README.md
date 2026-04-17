# Argument System

Handles parsing a single String input value into typed data.

## Development Notes

- The main abstraction in my Argument system is still `ArgumentType<T>`. I liked keeping everything centered around the idea of taking one `String` and parsing it into one typed value, because it kept the system consistent even as I added more features.
- Instead of putting everything into one large helper class, I kept the basic parsers split into smaller classes. That made the code easier to read and made it simpler to extend the system as I added more types.
- For Part 2, I added support for more general parsing cases instead of only the original basic types. In particular, I added `EnumArgumentType<T>` so the system can parse any enum without hardcoding a specific one, and `RegexStringArgumentType` so string validation can be based on a pattern instead of only fixed choices.
- One design change that mattered more this time was adding `ValidatedArgumentType<T>`. Instead of continuing to make a separate validation class for every new rule, I wanted a more reusable way to take an existing argument type and layer validation on top of it.
- I also added `ArgumentParseException` so the Argument system has a more consistent way to represent parsing and validation failures. Before that, everything was just throwing generic `RuntimeException`, which worked but did not really feel like a clear library design.
- I still think one weakness is that some error translation happens outside the Argument system, especially in the scenario layer. The Argument side is cleaner now, but the whole project still is not fully consistent about error handling from top to bottom.

## PoC Design Analysis

### Individual Review (Argument Lead)

- One design decision that worked well was using `ArgumentType<T>` as the base abstraction. It gave every parser the same overall shape, which made the system easier to extend and helped keep the design polymorphic instead of hardcoding special cases.
- Another good design decision was adding `ValidatedArgumentType<T>` as a reusable validation wrapper. That made the validation design stronger than it was before, because rules like ranges, regex checks, and choice checks do not all need completely separate logic anymore.
- One weaker design decision is that the system still has a mix of old specialized validation classes and the newer reusable wrapper-based approach. It works, but the design is a little in-between right now instead of being fully unified.
- Another weaker design decision is that the Argument system is clean for parsing one value at a time, but it still depends on the scenario layer to do some exception translation and presentation. That means the Argument side is not fully in control of how its errors are exposed in the final scenarios.

### Individual Review (Command Lead)

- One thing that looks promising is that the Command side can build on top of `ArgumentType<T>` instead of doing all parsing itself. That should reduce repeated logic later.

- Another good part is that positional and named arguments can stay a Command-system problem, while the Argument system stays focused on parsing one value at a time.

- One thing that still feels unclear is exactly how the Command system will store and use typed arguments internally. The Argument system is in place, but the connection between the two systems could be more clearly defined.

- Another concern is that because we built the PoC scenario-first, there is still some repeated parsing logic in the scenarios that a fuller Command API would probably want to clean up later.


### Team Review

- One design decision we still disagree on is how general the validation part of the Argument system should be. One option is keeping separate argument classes for common cases because they are easier to read, and the other is using a more reusable wrapper-based approach like `ValidatedArgumentType<T>`. I think the wrapper approach is stronger overall, but I can still see why the more direct class-based approach feels simpler.
- One design concern we both agree on is making sure the Argument and Command systems stay clearly separated while still fitting together well. The Argument side is supposed to handle parsing individual values, but the full project also needs typed extraction and clean scenario integration. That boundary mostly works right now, but I do not think we have a fully clean solution for it yet.
