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

- One design decision I still think worked well on the Argument side was building everything around `ArgumentType<T>`. That gave all of the parsers the same general shape, and it also meant the Command side could rely on one interface instead of needing special handling for every type.
- Another good design decision was adding `ValidatedArgumentType<T>`. That made the validation side feel a lot more reusable, because things like ranges, regex checks, and choice checks do not all need completely separate parsing logic anymore.

**Bad decisions (Argument System):**

- One weaker design decision is that the Argument system still has a mix of older specialized validation classes and the newer wrapper-based approach. It works, but the design feels a little in-between right now instead of being fully unified.
- Another weaker design decision is that some exception translation still happens outside the Argument system, especially in the scenario layer. The Argument side is cleaner now, but it still is not fully in control of how its errors are presented in the final scenarios.

**Good decision (Command System):**

- One good design decision in the Command system is separating the command definition from the parsed result with `Command` and `ParsedCommand`. I think that keeps the structure of a command and the values produced by parsing from getting mixed together, which feels like a cleaner direction as the project gets more complicated.

**Bad decision (Command System):**

- One weaker design decision in the Command system is that typed extraction is still not as strong as it probably should be. `ParsedCommand` still relies on unchecked casts and can also return a raw `Map<String, Object>`, so the library is not giving as much type safety or error clarity as it probably should.

---

## Team Review

**Current disagreement:**

We still disagree on how subcommand behavior should be handled going forward. Right now `dispatch` uses two separate `Command` parses to handle the fact that the type of `value` depends on `type`. It works but it is repetitive and puts coordination logic in the scenario. The other option is building subcommand support directly into `Command` so different argument structures can be registered per subcommand name. That would be a cleaner API but a significantly bigger change to implement. We have not agreed on which direction to take for MVP.

**Current design concern:**

We both agree that `ParsedCommand` does not give enough static type safety. You call `parsed.get("left")` and the compiler trusts that it is a `Double` because you said so, but there is nothing enforcing that at compile time. For a library that is supposed to make argument parsing safer and more structured, that feels like a real problem. We do not have a clean solution yet — a fully typed approach would probably require rethinking how arguments are declared and stored in `Command`, and we are not sure what that looks like in practice.
