# Command System

Handles creation of command structures and multi-argument parsing.

## Development Notes

Most commands were implemented using `Input.parseBasicArgs()`, which gave us a clean split between positional and named arguments. Hardest case for me was `search` because `parseBasicArgs` always expects a value after a `--flag`, so using it for `--case-insensitive` would always throw. Instead, we ended up using `Input.parseValue()` directly there to peek at the next token ourselves.

For type parsing we found out that on the existing `ArgumentType` classes. That kept things consistent and meant `BooleanArgumentType` handled the strict `true`/`false` validation for `search` for free.

The negative decimal quirk in `div` is intentional. We replicate the same behavior from the research project where `-2.0` fails but `-2` works.

Note: `Scenarios.parse` had a bug where a bare command with no arguments, such as `echo` by itself, would set `base` to an empty string and fall into the default error branch. This bug was fixed that by using the full command string as the base when there is no space.

## PoC Design Analysis

### Individual Review (Command Lead)

Good decisions:
- Using `Input.parseValue()` directly for `search`. Trying to use optional flags into `parseBasicArgs` would have required modifying the input system.
- Using the `ArgumentType` classes for parsing keeps the command methods thin and lets validation logic live in one place.

Less-good decisions:
- `dispatch` uses a hardcoded if branch to handle `static` vs `dynamic`. It works for now but will need a real subcommand abstraction for MVP.
- Returning `Map<String, Object>` means callers have no type safety. Getting an `int` out requires a cast, which is exactly the problem the library is supposed to solve.

### Individual Review (Argument Lead)

### Team Review

Default values are currently hardcoded per scenario (e.g. `echo` defaults to `"echo,echo,echo..."`). We need a cleaner general solution for MVP, something built into the command definition rather than scattered across methods. We also haven't fully decided how subcommands should work. The current `dispatch` implementation is a placeholder and will need to be rethought once we have a proper command builder in place.