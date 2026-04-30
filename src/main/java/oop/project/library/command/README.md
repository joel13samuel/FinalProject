# Command System

Handles creation of command structures and multi-argument parsing.

## Development Notes

The main abstraction in the Command system is the `Command` class itself. I liked keeping it centered around a simple builder pattern where you declare positional and named arguments one at a time, because it made each scenario easy to read and kept the command definitions self-contained.

Instead of trying to handle everything in one large parse method, I split the logic into separate phases: first collect all tokens, then validate and parse positionals, then validate and parse named arguments. That made the code easier to follow and easier to debug when something went wrong.

For Part 2, I added support for aliases through `addAlias(alias, canonical)` so the Command system handles alias resolution internally rather than pushing that work into the scenarios. I also added support for optional positionals and named arguments with defaults, so things like `echo` with no arguments work cleanly without special cases in the scenario.

State management was also improved: positional and named arguments are now stored as `PositionalArgument` and `NamedArgument` records that bundle the name, type, and default value together. This removed the previous separate lists and the `"__positional__"` prefix hack, and made it easier to reason about each argument as a single unit.

Subcommand support was added through `addSubcommand(name, subcommand)`, which lets a `Command` delegate to a different command structure based on the first input token. The `dispatch` scenario uses this directly — `static` and `dynamic` are registered as subcommands with different `ArgumentType` configurations, so the scenario never does any manual parsing itself.

I also standardized how exceptions are handled across all scenarios. Every scenario method wraps its logic in try/catch and rethrows from within the `scenarios` package, because the test framework checks that exceptions originate there.

One weakness I still see is the boolean no-value flag check inside `Command.parse` — detecting `instanceof Boolean` in the default value to decide whether a flag with no value is valid is technically the Command system making a type-specific decision. It works, but it is a design tradeoff worth noting.

## Feature Showcase

The `action` scenario demonstrates the subcommand abstraction built into `Command`. It registers two subcommands — `move` and `say` — each with a completely different argument structure:

- `action move 3 5` parses `x` and `y` as integers
- `action say hello` parses `message` as a string with an optional `--loud` boolean flag
- `action say hello -loud` sets the loud flag to true without a value

This shows that the Command system can dispatch to different typed argument definitions based on the first token, with no manual parsing or if-branching in the scenario itself. The full structure — including types, defaults, and flags — is declared in the command definition.

## Individual Review

### Command Lead

**Good decisions (Command System):**

- Alias resolution is built into `Command` through `addAlias(alias, canonical)` rather than being handled in each scenario. No matter which alias the user types, the result map always uses the canonical key. We could have handled this in the scenarios themselves, but putting it in the command definition keeps the scenarios cleaner and makes aliases reusable.
- The `Command` builder now validates configuration mistakes like duplicate argument names and invalid aliases when the command is declared, not at parse time. That catches misuse eagerly and gives a clear error message at the point where the mistake was made.

**Bad decisions (Command System):**

- The boolean no-value flag behavior is detected by checking `instanceof Boolean` on the default value inside `parse`. That means the Command system is making a type-specific decision instead of delegating it to the argument type. A cleaner design would let the `ArgumentType` signal whether it supports no-value input.
- `ParsedCommand` stores values in a `Map<String, Object>` internally. The checked `get(name, Class<T>)` helps, but the structure is still runtime-typed. A fully statically typed result would require rethinking how command definitions are stored.

**Good decision (Argument System):**

- `ValidatedArgumentType<T>` is a strong design decision. Instead of making a new class every time a new validation rule is needed, the system can wrap any existing type and layer reusable validation on top.

**Bad decision (Argument System):**

- The public API still includes both generic validation building blocks and convenience wrappers for common cases. That is useful for readability, but it means there is still some tension around which layer users should prefer.

---

### Argument Lead

**Good decisions (Argument System):**

- One design decision that worked well on the Argument side was building everything around `ArgumentType<T>`. That gave all of the parsers the same general shape, and it also meant the Command side could rely on one interface instead of needing special handling for every type.
- Another good design decision was separating validation into reusable `ValidationRule<T>` objects. That made the validation side feel a lot more reusable, because things like ranges, regex checks, and choice checks do not all need completely separate parsing logic anymore.

**Bad decisions (Argument System):**

- One weaker design decision is that the API still exposes both convenience wrappers and lower-level generic validation types. The implementation is unified now, but the surface area still gives multiple ways to do similar things.
- Another weaker decision is that some exception translation still happens outside the Argument system, especially in the scenario layer. The Argument side is cleaner now, but it still is not fully in control of how its errors are presented in the final scenarios.

**Good decision (Command System):**

- Separating the command definition from the parsed result with `Command` and `ParsedCommand` keeps the structure of a command and the values produced by parsing from getting mixed together. `ParsedCommand.get(name, Class<T>)` makes typed extraction clearer and gives a useful error message when the wrong type is requested.

**Bad decision (Command System):**

- State in `Command` is mutable throughout the object's lifetime since there is no way to finalize or lock a command definition after construction. That means nothing prevents adding arguments after a parse has already been called, which could lead to inconsistent behavior.

---

## Team Review

**Current disagreement:**

We disagree on how much the `Command` API should enforce about command structure at definition time versus parse time. One view is that commands should be as easy to define as possible, with validation happening when `parse` is called. The other view is that more constraints should be caught eagerly — for example, preventing required positionals from being added after optional ones. We have not settled on how strict the builder should be.

**Current design concern:**

We both agree that `ParsedCommand` does not give enough static type safety. `get(name, Class<T>)` gives clearer runtime errors, but the structure is still a runtime map rather than a statically typed command result. For a library that is supposed to make argument parsing safer and more structured, that still feels like a real design limit. We do not have a clean solution yet — a fully typed approach would probably require rethinking how arguments are declared and stored in `Command`, and we are not sure what that looks like in practice.