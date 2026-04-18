# Command System

Handles creation of command structures and multi-argument parsing.

## Development Notes

The main abstraction in the Command system is the `Command` class itself. I liked keeping it centered around a simple builder pattern where you declare positional and named arguments one at a time, because it made each scenario easy to read and kept the command definitions self-contained.

Instead of trying to handle everything in one large parse method, I split the logic into separate phases: first collect all tokens, then validate and parse positionals, then validate and parse named arguments. That made the code easier to follow and easier to debug when something went wrong.

For Part 2, I added support for aliases through `addAlias(alias, canonical)` so the Command system handles alias resolution internally rather than pushing that work into the scenarios. I also added support for optional positionals and named arguments with defaults, so things like `echo` with no arguments work cleanly without special cases in the scenario.

One design change that mattered more this time was making sure all parsing goes through the `Command` system rather than having scenarios call `ArgumentType.parse()` directly. The `dispatch` scenario is a good example of this — it uses a two-pass approach where the first parse gets the type and the second parse uses the right `ArgumentType` for the value, so the scenario itself never does any manual parsing.

I also standardized how exceptions are handled across all scenarios. Every scenario method wraps its logic in try/catch and rethrows from within the `scenarios` package, because the test framework checks that exceptions originate there. Before that change, failures deeper in the library were being flagged as unexpected.

One weakness I still see is that default values are declared inline per scenario rather than being a general feature of the `Command` definition. It works, but it is not as clean as it could be, and it means the defaults are scattered across the scenario methods instead of living in one place.

## Individual Review

### Command Lead

**Good decisions (Command System):**

- Alias resolution is built into `Command` through `addAlias(alias, canonical)` rather than being handled in each scenario. No matter which alias the user types, the result map always uses the canonical key. We could have handled this in the scenarios themselves, but putting it in the command definition keeps the scenarios cleaner and makes aliases reusable.
- The `Command` builder separates positional and named argument declarations clearly. Positionals are ordered and index-based, named arguments are looked up by key. That separation made the parsing logic easier to reason about and mirrors how most real CLI tools work.

**Bad decisions (Command System):**

- Positional and named defaults are stored in the same map with a `"__positional__"` prefix to tell them apart. It works, but it is a hack. They are conceptually different and probably should have been stored separately from the beginning.
- The `Command` class does not validate that argument names are unique when they are added. If you accidentally register two positionals with the same name, you will not find out until something breaks at runtime. A better design would catch that kind of mistake at definition time.

**Good decision (Argument System):**

- `ValidatedArgumentType<T>` is a strong design decision. Instead of making a new class every time a new validation rule is needed, you can wrap any existing type and layer validation on top. That is a much more extensible approach than the class-per-case pattern it replaces.

**Bad decision (Argument System):**

- The system still has a mix of older specialized validation classes and the newer wrapper-based approach. The design is a little in-between right now, which makes it harder to know which pattern to follow when adding something new.

---

### Argument Lead

**Good decisions (Argument System):**

- Using `ArgumentType<T>` as the base abstraction gave every parser the same shape. The Command system only depends on that interface, so adding new types does not require changing anything on the Command side.
- Adding `ValidatedArgumentType<T>` made the validation design more reusable. Range checks, regex checks, and choice checks do not all need their own separate logic anymore, which made the system easier to extend in Part 2.

**Bad decisions (Argument System):**

- `BooleanArgumentType` only accepts `"true"` or `"false"` as strings. That works for typed named arguments, but it cannot handle no-value flags like `-case-insensitive`. The Command system ended up handling that case manually, which meant the argument type was not doing the full job.
- Some exception translation still happens outside the Argument system, in the scenario layer. The Argument side is cleaner now, but the full project is still not fully consistent about where errors are caught and how they are presented.

**Good decision (Command System):**

- The two-pass approach in `dispatch` keeps all parsing inside the `Command` system. The scenario never calls `ArgumentType.parse()` directly, which keeps the separation between the two systems clear even for a case like subcommands where the argument types depend on a previous argument's value.

**Bad decision (Command System):**

- `ParsedCommand.toMap()` returns a raw `Map<String, Object>`. If you call `get` with the wrong type you get a `ClassCastException` at runtime with no helpful context. For a library whose purpose is safer argument parsing, that is a real gap in the design.

---

## Team Review

**Current disagreement:**

We still disagree on how subcommand behavior should be handled going forward. Right now `dispatch` uses two separate `Command` parses to handle the fact that the type of `value` depends on `type`. It works but it is repetitive and puts coordination logic in the scenario. The other option is building subcommand support directly into `Command` so different argument structures can be registered per subcommand name. That would be a cleaner API but a significantly bigger change to implement. We have not agreed on which direction to take for MVP.

**Current design concern:**

We both agree that `ParsedCommand` does not give enough static type safety. You call `parsed.get("left")` and the compiler trusts that it is a `Double` because you said so, but there is nothing enforcing that at compile time. For a library that is supposed to make argument parsing safer and more structured, that feels like a real problem. We do not have a clean solution yet — a fully typed approach would probably require rethinking how arguments are declared and stored in `Command`, and we are not sure what that looks like in practice.