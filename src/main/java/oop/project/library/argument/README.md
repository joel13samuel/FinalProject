# Argument System

Handles parsing a single String input value into typed data.

## Development Notes

- The main abstraction in my Argument system is still `ArgumentType<T>`. I liked keeping everything centered around the idea of taking one `String` and parsing it into one typed value, because it kept the system consistent even as I added more features.
- Instead of putting everything into one large helper class, I kept the basic parsers split into smaller classes. That made the code easier to read and made it simpler to extend the system as I added more types.
- For Part 2, I added support for more general parsing cases instead of only the original basic types. In particular, I added `EnumArgumentType<T>` so the system can parse any enum without hardcoding a specific one, and `RegexStringArgumentType` so string validation can be based on a pattern instead of only fixed choices.
- One design change that mattered more this time was separating validation into reusable `ValidationRule<T>` objects and then composing those through `ValidatedArgumentType<T>`. That let me keep parsing and validation separate while still making them work together cleanly.
- I also added `RangedArgumentType<T>` with a shared `RangeValidationRule<T>` so numeric ranges are modeled as one reusable abstraction instead of duplicated integer-vs-double logic.
- I also added `ArgumentParseException` so the Argument system has a more consistent way to represent parsing and validation failures. Before that, everything was just throwing generic `RuntimeException`, which worked but did not really feel like a clear library design.
- I still think one weakness is that some error translation happens outside the Argument system, especially in the scenario layer. The Argument side is cleaner now, but the whole project still is not fully consistent about error handling from top to bottom.
- A good example of that abstraction is the `window` scenario siunce it uses the same range validation design with `LocalDate` instead of only `Integer` or `Double`.

## MVP / Showcase Scenarios

For the Argument side MVP work, the `rank` scenario is the one we used to show regex string validation. It runs through the library and only accepts values in a specific format.

For the Argument side feature showcase, the `window` scenario shows that our range validation design doesn't have to stick to numbers. It uses `RangedArgumentType<T>` with `CustomArgumentType<LocalDate>` to check whether a parsed date falls within an allowed range. This goes further than the original assignment spec because the range feature was only meant for `Integer` and `Double`.

## PoC Design Analysis

### Individual Review (Argument Lead)

- One design decision that worked well was using `ArgumentType<T>` as the base abstraction. It gave every parser the same overall shape, which made the system easier to extend and helped keep the design polymorphic instead of hardcoding special cases.
- Another good design decision was adding `ValidationRule<T>` and `ValidatedArgumentType<T>`. That made the validation design stronger than it was before, because rules like ranges, regex checks, and choice checks can now share one validation model instead of each inventing their own logic.
- One weaker design decision is that I still keep convenience wrappers like `RangedIntegerArgumentType` and `ChoiceStringArgumentType` for readability. They now delegate to shared abstractions, but the public API still gives users multiple ways to express similar validation.
- Another weaker design decision is that the Argument system is clean for parsing one value at a time, but it still depends on the scenario layer to do some exception translation and presentation. That means the Argument side is not fully in control of how its errors are exposed in the final scenarios.

### Individual Review (Command Lead)

- One thing that looks promising is that the Command side can build on top of `ArgumentType<T>` instead of doing all parsing itself. That should reduce repeated logic later.

- Another good part is that positional and named arguments can stay a Command-system problem, while the Argument system stays focused on parsing one value at a time.

- One thing that still feels unclear is exactly how the Command system will store and use typed arguments internally. The Argument system is in place, but the connection between the two systems could be more clearly defined.

- Another concern is that dynamic argument structures like `dispatch` still require coordination in the scenario layer. The Argument system composes well, but subcommand-like behavior still lives above it instead of being modeled directly in the Command API.


### Team Review

- One design decision we still disagree on is how much convenience API should sit on top of the generic validation model. One option is exposing mostly reusable building blocks like `ValidationRule<T>` and `RangedArgumentType<T>`, and the other is also keeping named convenience wrappers for common cases to make usage more readable.
- One design concern we both agree on is making sure the Argument and Command systems stay clearly separated while still fitting together well. The Argument side is supposed to handle parsing individual values, but the full project also needs typed extraction and clean scenario integration. That boundary mostly works right now, but I do not think we have a fully clean solution for it yet.
