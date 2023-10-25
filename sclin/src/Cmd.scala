package sclin

import better.files.*
import monix.execution.Scheduler.Implicits.global
import scala.annotation.switch
import scala.concurrent.duration.*
import spire.math.*
import ANY.*

extension (env: ENV)

  def cmd1(x: String): ENV = (x: @switch) match

    // CMDOC START

    case "("   => env.startFN
    case ")"   => env
    case ")~"  => env.evalTASK
    case ")!"  => env.evalTRY
    case ")#"  => env.locId
    case ")##" => env.globId
    case ")="  => env.lambda
    case "["   => env.startARR
    case "]"   => env.endARR
    case "]:"  => env.endMAP
    case "/]"  => env.endARR.getn
    case "/]=" => env.endARR.setn
    case "/]%" => env.endARR.setmodn
    case "]*"  => env.endMAP.gets
    case "]="  => env.endMAP.sets
    case "]%"  => env.endMAP.setmods
    case "."   => env.dot

    /*
    @s a -> STR
    Type of `a`.
     */
    case "type" => env.getType
    /*
    @s a -> STR
    `a` as formatted string.
     */
    case "form" => env.form
    /*
    @s a -> STR
    Converts `a` to `SEQ`.
     */
    case ">Q" => env.envSEQ
    /*
    @s a -> ARR
    Converts `a` to `ARR`.
     */
    case ">A" => env.envARR
    /*
    @s a -> ARR
    Converts `a` to `MAP`.
     */
    case ">M" => env.envMAP
    /*
    @s a' -> STR'
    Atomic #{>S}.
     */
    case "S" => env.vSTR
    /*
    @s a -> STR
    Converts `a` to `STR`.
     */
    case ">S" => env.envSTR
    /*
    @s a -> NUM
    Converts `a` to `NUM`.
     */
    case ">N" => env.envNUM
    /*
    @s a' -> NUM'
    Atomic #{>N}.
     */
    case "N" => env.vNUM
    /*
    @s a' -> DBL'
    Atomic #{>D}.
     */
    case "D" => env.vDBL
    /*
    @s a -> DBL
    Converts `a` to `DBL`.
     */
    case ">D" => env.envDBL
    /*
    @s a -> FN
    Converts `a` to `FN`.
     */
    case ">F" => env.envFN
    /*
    @s (a >STR) (b >STR) -> ERR
    Converts `a` to `ERR` with message `b`.
     */
    case ">E" => env.envERR
    /*
    @s a -> TASK
    Converts `a` to `TASK`.
     */
    case ">~" => env.envTASK
    /*
    @s a -> FUT
    Converts `a` to `FUT`.
     */
    case "~>" => env.envFUT
    /*
    @s a -> OBS
    Converts `a` to `OBS`.
     */
    case ">~*" => env.envOBS
    /*
    @s a -> TRY
    Converts `a` to `TRY`.
     */
    case ">!?" => env.envTRY
    /*
    @s a -> TF
    Converts `a` to `TF`.
     */
    case ">?" => env.envTF
    // TODO: docs
    case "~>?" => env.otoTF
    /*
    @s (a >NUM) (b >NUM)' -> STR
    Converts `a` to an `STR` formatted to `b`'s specifications.
     */
    case "N>d" => env.toNUMD
    /*
    @s a b -> _
    Converts `a` to type of `b`.
     */
    case ">TT" => env.matchType
    /*
    @s (a >STR)' -> MAP
    #{>M} using a multiline string.
    Each line of `a` is #{#}ed, and the resulting top 2 stack items form each key-value pair.
    ```sclin
    `` >>M
    "a" 1
    "b" 2
    "c" 3
    `
    ```
     */
    case ">>M" => env.lineMAP
    /*
    @s (a >STR)' -> _
    Converts `a` from JSON to `ANY`.
    ```sclin
    g; js>
    {"a": 1, "b": 2, "c": [3, 4]}
    ```
     */
    case "js>" => env.fromJSON
    /*
    @s a -> STR
    Converts `a` from `ANY` to JSON.
    ```sclin
    ["a" 1, "b" 2, "c" [3 4] , ]: >js
    ```
     */
    case ">js" => env.toJSON

    /*
    @s -> UN
    `UN`
     */
    case "UN" => env.push(UN)
    /*
    @s -> TF
    True.
     */
    case "$T" => env.push(TF(true))
    /*
    @s -> TF
    False.
     */
    case "$F" => env.push(TF(false))
    /*
    @s -> FN
    Empty `FN`.
     */
    case "()" => env.push(UN.toFN(env))
    /*
    @s -> ARR
    Empty `ARR`.
     */
    case "[]" => env.push(UN.toARR)
    /*
    @s -> MAP
    Empty `MAP`.
     */
    case "[]:" => env.push(UN.toMAP)
    /*
    @s -> TASK
    Empty `TASK`.
     */
    case "()~" => env.push(UN.toTASK)
    /*
    @s -> TRY
    Empty `TRY`.
     */
    case "()!" => env.push(UN.toTRY)
    /*
    @s -> NUM
    π (Pi).
     */
    case "$PI" => env.push(NUM(Real.pi))
    /*
    @s -> NUM
    e (Euler's number).
     */
    case "$E" => env.push(NUM(Real.e))
    /*
    @s -> NUM
    Φ (Golden Ratio).
     */
    case "$PHI" => env.push(NUM(Real.phi))
    /*
    @s -> NUM
    Uniformly random number.
     */
    case "$rng" => env.push(NUM(random))
    /*
    @s -> NUM
    Current line number of program execution.
     */
    case "$LINE" => env.getLNum
    /*
    @s -> STR
    Current file of program execution.
     */
    case "$FILE" => env.getLFile
    /*
    @s -> STR
    Current working directory at start of program execution.
     */
    case "$PWD/" => env.push(Dsl.pwd.toString.sSTR)
    /*
    @s -> STR
    Current working directory at current state in program execution.
     */
    case "$CWD/" => env.push(Dsl.cwd.toString.sSTR)
    /*
    @s -> STR
    Home directory.
     */
    case "$~/" => env.push(File.home.toString.sSTR)
    /*
    @s -> SEQ[NUM*]
    Infinite `SEQ` of 0 to ∞.
     */
    case "$W" => env.push(wholes.toSEQ)
    /*
    @s -> SEQ[NUM*]
    Infinite `SEQ` of 1 to ∞.
     */
    case "$N" =>
      env.push(wholes.drop(1).toSEQ)
    /*
    @s -> SEQ[NUM*]
    Infinite `SEQ` of primes.
     */
    case "$P" => env.push(prime.lazyList.map(NUM(_)).toSEQ)
    /*
    @s -> ARR[STR*]
    `ARR` of lines of currently-executing file.
     */
    case "$L*" => env.getLns
    /*
    @s -> STR
    `UPPERCASE` alphabet.
     */
    case "$ABC" => env.push(STR("ABCDEFGHIJKLMNOPQRSTUVWXYZ"))
    /*
    @s -> STR
    `lowercase` alphabet.
     */
    case "$abc" => env.push(STR("abcdefghijklmnopqrstuvwxyz"))
    /*
    @s -> STR | UN
    Current line.
     */
    case "g@" => env.getLHere
    /*
    @s -> STR | UN
    Next line.
     */
    case "g;" => env.getLNext
    /*
    @s -> STR | UN
    Previous line.
     */
    case "g;;" => env.getLPrev
    /*
    @s -> STR
    Newline character.
     */
    case "n\\" => env.push(STR("\n"))
    /*
    @s -> NUM
    Number of milliseconds since UNIX epoch (January 1, 1970 00:00:00 UTC).
     */
    case "$NOW" => env.push(NUM(global.clockRealTime(MILLISECONDS)))

    /*
    @s (a >FN)' ->
    Loads ID `a` into local scope.
    ```sclin
    "outer"=$a ( \a @$ a ) # $a
    #a "inner"
    ```
     */
    case "@$" => env.locId
    /*
    @s (a >FN) ->
    Loads ID `a` into global scope.
    ```sclin
    \a @$$ ( "inner" =$a $a ) # a
    #a "outer"
    ```
     */
    case "@$$" => env.globId
    /*
    @s (a >FN) -> STR | UN
    #{@$} and get as `STR`.
     */
    case "@:" => env.locIdS
    /*
    @s (a >FN) -> STR | UN
    #{@$$} and get as `STR`.
     */
    case "@::" => env.globIdS
    /*
    @s (a >FN) -> FN | UN
    #{@$} and get as `FN`.
     */
    case "@;" => env.locIdF
    /*
    @s (a >FN) -> FN | UN
    #{@$$} and get as `FN`.
     */
    case "@;;" => env.globIdF
    /*
    @s x* (a >FN) -> _*
    #{@;} and #{#}
     */
    case "@#" => env.locIdF.eval
    /*
    @s x* (a >FN) -> _*
    #{@;;} and #{#}.
     */
    case "@##" => env.globIdF.eval
    /*
    @s _* (a >FN) -> _*
    Stores stack items into local variables defined by `a`.
    Somewhat analogous to function arguments in other languages.
    ```sclin
    1 2 3 ;
    ( a b c ) -> $c $b $a
    ```
     */
    case "->" => env.lambda

    /*
    @s -> STR
    Line from STDIN.
     */
    case "i>" => env.in
    /*
    @s (a >STR) ->
    Sends `a` to STDOUT.
     */
    case ">o" => env.out
    /*
    @s (a >STR) ->
    #{>o}s `a` with trailing newline.
     */
    case "n>o" => env.outn
    /*
    @s a ->
    #{form}s and #{n>o}s `a`.
     */
    case "f>o" => env.outf

    /*
    @s a -> ARR
    Gets filepath `a` in segments.
     */
    case "_/<>" => env.pathARR
    /*
    @s a -> STR
    Gets filepath `a` as a string.
     */
    case "_/><" => env.pathSTR
    /*
    @s a -> STR
    Gets the filename of filepath `a`.
     */
    case "_/x" => env.pathname
    /*
    @s a -> STR
    Gets the basename of filepath `a`.
     */
    case "_/x_" => env.pathbase
    /*
    @s a -> STR
    Gets the file extension of filepath `a`.
     */
    case "_/_x" => env.pathext

    // TODO: docs
    // slowest but works for everything
    case "fs>" => env.fsread
    // TODO: docs
    // fastest but most situational
    case "fs>b" => env.fsreadb
    // TODO: docs
    // decently fast, requires encoding param
    case "fs>n" => env.fsreadn

    // TODO: docs
    case "b>S" => env.btou
    // TODO: docs
    case "S>b" => env.utob
    // TODO: docs
    case "~b>S" => env.oBtoU
    // TODO: docs
    case "~S>b" => env.oUtoB

    /*
    @s a -> a a
     */
    case "dup" => env.dup
    /*
    @s a* -> a* ARR[a*]
     */
    case "dups" => env.dups
    /*
    @s a b -> a a b
     */
    case "dupd" => env.dupd
    /*
    @s a b -> a b a
     */
    case "over" => env.over
    /*
    @s a b -> a b a b
     */
    case "ddup" => env.ddup
    /*
    @s a b c -> a b c a b c
     */
    case "edup" => env.edup
    /*
    @s (a @ n) b* (n >NUM) -> a b* a
    #{dup}s `n`th item from top of stack.
    ```sclin
    4 3 2 1 0 3pick
    ```
    ```sclin
    4 3 2 1 0 1_ pick
    ```
     */
    case "pick" => env.pick
    /*
    @s _ ->
     */
    case "pop" => env.pop
    /*
    @s _* ->
     */
    case "clr" => env.clr
    /*
    @s _ b -> b
     */
    case "nip" => env.nip
    /*
    @s _ _ ->
     */
    case "ppop" => env.pop.pop
    /*
    @s _ _ _ ->
     */
    case "qpop" => env.pop.pop.pop
    /*
    @s (a @ n) b* (n >NUM) -> _*
    #{pop}s `n`th item from top of stack.
     */
    case "nix" => env.nix
    /*
    @s a b -> b a
     */
    case "swap" => env.swap
    /*
    @s a* -> _*
    Reverses stack.
     */
    case "rev" => env.rev
    /*
    @s a b c -> b a c
     */
    case "swapd" => env.swapd
    /*
    @s a b -> b a b
     */
    case "tuck" => env.tuck
    /*
    @s (a @ n) b* c (n >NUM) -> c b* a
    #{swap}s `c` with `n`th item from top of stack.
    ```sclin
    4 3 2 1 0 3trade
    ```
    ```sclin
    4 3 2 1 0 1_ trade
    ```
     */
    case "trade" => env.trade
    /*
    @s a b c -> b c a
     */
    case "rot" => env.rot
    /*
    @s a b c -> c a b
     */
    case "rot_" => env.rotu
    /*
    @s (a @ n) b* (n >NUM) -> b* a
    #{rot}s to top `n`th item from top of stack.
    ```sclin
    4 3 2 1 0 3roll
    ```
    ```sclin
    4 3 2 1 0 1_ roll
    ```
     */
    case "roll" => env.roll
    /*
    @s b* c (n >NUM) -> (c @ n) b*
    #{rot_}s `c` to `n`th from top of stack.
    ```sclin
    4 3 2 1 0 3roll_
    ```
    ```sclin
    4 3 2 1 0 1_ roll_
    ```
     */
    case "roll_" => env.rollu
    /*
    @s a* b (f >FN) -> _* b
    #{pop}s `b`, #{#}s `f`, and pushes `b`.
     */
    case "dip" => env.dip
    /*
    @s a -> FN[a]
    Wraps `a` in `FN`.
     */
    case "\\" => env.wrapFN
    /*
    @s a* f -> _*
    Executes `f`.
    ```sclin
    1 2 ( 3 + 4 ) #
    ```
     */
    case "#" => env.eval
    /*
    @s f' -> _'
    Evaluates `f` (#{#} but only preserves resulting top of stack).
    ```sclin
    1 2 ( dups 3+` ) Q
    ```
     */
    case "Q" => env.quar
    /*
    @s a* (n >NUM) -> _*
    #{#}s `n`th line.
     */
    case "@@" => env.evalLine
    /*
    @s a* (n >NUM) -> _*
    #{#}s `n`th line relative to current line.
     */
    case "@~" => env.evalLRel
    /*
    @s a* -> _*
    #{#}s current line.
     */
    case "@" => env.evalLHere
    /*
    @s a* -> _*
    #{#}s next line.
     */
    case ";" => env.evalLNext
    /*
    @s a* -> _*
    #{#}s previous line.
     */
    case ";;" => env.evalLPrev
    /*
    @s (n >NUM) -> STR | UN
    `n`th line.
     */
    case "g@@" => env.getLn
    /*
    @s (n >NUM) -> STR | UN
    `n`th line relative to current line.
     */
    case "g@~" => env.getLRel
    /*
    @s a* (b >TF) f -> _*
    #{#}s `f` if `b` is truthy.
     */
    case "&#" => env.evalAnd
    /*
    @s a* (b >TF) f -> _*
    Non-destructive #{&#};
    executes `f` on `b` if `b` is truthy,
    otherwise keeps `b` on stack.
     */
    case "&&#" => env.evalAnd$
    /*
    @s a* (b >TF) f -> _*
    #{#}s `f` if `b` is falsy.
     */
    case "|#" => env.evalOr
    /*
    @s a* (b >TF) f -> _*
    Non-destructive #{|#};
    executes `f` on `b` if `b` is falsy,
    otherwise keeps `b` on stack.
     */
    case "||#" => env.evalOr$
    /*
    @s a* (b >TF) f g -> _*
    #{#}s `f` if `b` is truthy; else #{#}s `g`.
     */
    case "?#" => env.evalIf
    /*
    @s a* (b >MAP) -> _*
    Iterates through each key-value pair of `b`.
    For each pair: if the #{Q} of the key is truthy,
    then #{#}s the value and short-circuits.
     */
    case "??#" => env.evalIf$
    /*
    @s a* f (n >NUM) -> _*
    #{#}s `f` `n` times.
     */
    case "*#" => env.evalTimes
    /*
    @s a* f g -> _*
    Tries to #{#} `f`; on error, pushes caught `ERR` and #{#}s `g`.
     */
    case "!#" => env.evalTry
    /*
    @s f' -> TRY'
    #{Q}s `f` and wraps the result in a `TRY`.
     */
    case "!Q" => env.evalTRY
    /*
    @s f' -> TASK'
    #{Q}s `f` asynchronously, returning a future.
     */
    case "~Q" => env.evalTASK
    /*
    @s (e ERR) ->
    Throws `e`.
     */
    case ">!" => env.throwERR
    /*
    @s (a >ARR) f -> ARR
    #{#}s `f` on `a` as if it were a stack.
    ```sclin
    [1 2 3 4] ( 5 swap ) '
    ```
     */
    case "'" => env.evalArrSt
    /*
    @s (a* >ARR) f -> _*
    #{#}s `f` on the stack as if it were an `ARR`.
    ```sclin
    1 2 3 4 1.+.map '_
    ```
     */
    case "'_" => env.evalStArr
    /*
    @s ->
    Clears code queue, similar to the "break" keyword in other languages.
     */
    case "end" => env.modCode(_ => LazyList())

    /*
    @s (a >NUM)' (b >NUM)' -> NUM'
    `a * 10 ^ b`
     */
    case "E" => env.scale
    /*
    @s (a >NUM)' -> NUM'
    Rounds `a` towards 0.
     */
    case "I" => env.trunc
    /*
    @s (a >NUM)' -> TF'
    Whether `a` is an integer.
     */
    case "I?" => env.isInt
    /*
    @s (a >NUM)' -> NUM'
    Rounds `a` towards -∞.
     */
    case "|_" => env.floor
    /*
    @s (a >NUM)' -> NUM'
    Rounds `a` to nearest integer.
     */
    case "|~" => env.round
    /*
    @s (a >NUM)' -> NUM'
    Rounds `a` towards ∞.
     */
    case "|^" => env.ceil
    /*
    @s (a >NUM)' (b >NUM)' -> ARR[NUM*]'
    Converts `a` from decimal to `ARR` of base-`b` digits.
    ```sclin
    153 2X>b
    ```
    ```sclin
    153 16X>b
    ```
     */
    case "X>b" => env.fromDec
    /*
    @s (a >ARR[>NUM*]) (b >NUM)' -> NUM'
    Converts base-`b` digits to decimal.
    ```sclin
    [1 0 0 1 1 0 0 1] 2b>X
    ```
    ```sclin
    [9 9] 16b>X
    ```
     */
    case "b>X" => env.toDec
    /*
    @s (a >NUM)' -> ARR[NUM NUM]'
    Converts `a` to a numerator-denominator pair.
    ```sclin
    4 6/ >n/d
    ```
    ```sclin
    $PI >n/d
    ```
     */
    case ">n/d" => env.toNumDen
    /*
    @s (a >NUM)' -> TF'
    Whether `a` is an exact value (i.e. represented in full precision).
    ```sclin
    2 3/ prec?
    ```
    ```sclin
    $PI prec?
    ```
     */
    case "prec?" => env.isExact

    /*
    @s (a >NUM)' -> NUM'
    `-a`
     */
    case "_" => env.neg
    /*
    @s (a >STR)' -> STR'
    Atomic #{_`}.
     */
    case "__" => env.neg$
    /*
    @s a -> _
    Reverses `a`.
     */
    case "_`" => env.neg$$
    /*
    @s (a >NUM)' (b >NUM)' -> NUM'
    `a + b`
     */
    case "+" => env.add
    /*
    @s (a >STR)' (b >STR)' -> STR'
    Atomic #{+`}.
     */
    case "++" => env.add$
    /*
    @s a b -> _
    Concatenates `a` and `b`.
     */
    case "+`" => env.add$$
    /*
    @s a (b _[_*]) -> _[a, b*]
    Prepends `a` to `b`.
     */
    case "<+" => env.cons
    /*
    @s (a _[_*]) b -> _[a*, b]
    Appends `b` to `a`.
     */
    case "+>" => env.snoc
    /*
    @s _[a, b*] -> a _[b*]
    Uncons; push first item and rest of `a`.
     */
    case "+<" => env.uncons
    /*
    @s a -> _[_*] _
    Unsnoc; push last item and rest of `a`.
     */
    case ">+" => env.unsnoc
    /*
    @s (a >NUM)' (b >NUM)' -> NUM'
    `a - b`
     */
    case "-" => env.sub
    /*
    @s (a >STR)' (b >STR)' -> STR'
    Atomic #{-`}.
     */
    case "--" => env.sub$
    /*
    @s a b -> _
    Remove occurrences of `b` from `a`.
    If `a` is `MAP`, then removal is performed on keys instead of values.
    ```sclin
    [1 2 3 4] 2-`
    ```
    ```sclin
    [0 1, 2 3, ]: 2-`
    ```
     */
    case "-`" => env.sub$$
    /*
    @s (a >NUM)' (b >NUM)' -> NUM'
    `a * b`
     */
    case "*" => env.mul
    /*
    @s (a >STR)' (b >NUM)' -> STR'
    Atomic #{*`}.
     */
    case "**" => env.mul$
    /*
    @s a b -> _
    `a` replicated according to `b`.
    If `b` is iterable, then `a` and `b` are recursively zipped together and replicated.
    ```sclin
    [1 2 3 4] [0 2 0 3] *` >A
    ```
    ```sclin
    [1 2 3 4] 3*` >A
    ```
     */
    case "*`" => env.mul$$
    /*
    @s (a >NUM)' (b >NUM)' -> NUM'
    `a / b`. Throws error if `b` is 0.
     */
    case "/" => env.div
    /*
    @s (a >NUM)' (b >NUM)' -> NUM'
    Integer #{/}.
     */
    case "/~" => env.divi
    /*
    @s (a >STR)' (b >NUM)' -> SEQ[STR*]'
    Atomic #{/`}.
     */
    case "//" => env.div$
    /*
    @s a (b >NUM)' -> SEQ
    `a` chunked to size `b`.
    ```sclin
    [1 2 3 4 5] 2/` >A
    ```
     */
    case "/`" => env.div$$
    /*
    @s (a >NUM)' (b >NUM)' -> NUM'
    `a (mod b)`
     */
    case "%" => env.mod
    /*
    @s (a >NUM)' (b >NUM)' -> NUM' NUM'
    Results of #{/~} and #{%} on `a` and `b`.
     */
    case "/%" => env.divmod
    /*
    @s (a >STR)' (b >NUM)' -> SEQ[STR*]'
    Atomic #{%`}.
     */
    case "%%" => env.mod$
    /*
    @s a (b >NUM)' -> SEQ
    `a` windowed to size `b`.
    ```sclin
    [1 2 3 4 5] 3%` >A
    ```
     */
    case "%`" => env.mod$$
    /*
    @s a (b >NUM)' (c >NUM)' -> SEQ
    #{%`} with extra skip parameter `c`.
    ```sclin
    [1 2 3 4 5] 3 2%` >A
    ```
     */
    case "%`~" => env.mod2$$
    /*
    @s (a >NUM)' (b >NUM)' -> NUM'
    `a ^ b`. Throws error if result would be a complex number.
     */
    case "^" => env.pow
    /*
    @s (a >NUM)' (b >NUM)' -> NUM'
    #{^} but `b` is coerced to `int`.
     */
    case "^~" => env.powi
    /*
    @s (a >STR)' (b >NUM)' -> SEQ[STR*]'
    Atomic #{^`}.
     */
    case "^^" => env.pow$
    /*
    @s a (n >NUM)' -> SEQ'
    Cartesian power of seed `a` to `n`.
    ```sclin
    "abc" 3^` >A
    ```
     */
    case "^`" => env.pow$$

    /*
    @s (a >NUM)' -> NUM'
    `e ^ a`
     */
    case "e^" => env.exp
    /*
    @s (a >NUM)' -> NUM'
    Absolute value of `a`.
     */
    case "abs" => env.abs
    /*
    @s (a >NUM)' -> NUM'
    Sine of `a`.
     */
    case "sin" => env.sin
    /*
    @s (a >NUM)' -> NUM'
    Cosine of `a`.
     */
    case "cos" => env.cos
    /*
    @s (a >NUM)' -> NUM'
    Tangent of `a`.
     */
    case "tan" => env.tan
    /*
    @s (a >NUM)' -> NUM'
    Arcsine of `a`.
     */
    case "sin_" => env.asin
    /*
    @s (a >NUM)' -> NUM'
    Arccosine of `a`.
     */
    case "cos_" => env.acos
    /*
    @s (a >NUM)' -> NUM'
    Arctangent of `a`.
     */
    case "tan_" => env.atan
    /*
    @s (a >NUM)' (b >NUM)' -> NUM'
    Arctangent of `a` with `b` as quadrant.
     */
    case "tan_II" => env.atan2
    /*
    @s (a >NUM)' -> NUM'
    Hyperbolic sine of `a`.
     */
    case "sinh" => env.sinh
    /*
    @s (a >NUM)' -> NUM'
    Hyperbolic cosine of `a`.
     */
    case "cosh" => env.cosh
    /*
    @s (a >NUM)' -> NUM'
    Hyperbolic tangent of `a`.
     */
    case "tanh" => env.tanh
    /*
    @s (a >NUM)' -> NUM'
    Hyperbolic arcsine of `a`.
     */
    case "sinh_" => env.asinh
    /*
    @s (a >NUM)' -> NUM'
    Hyperbolic arccosine of `a`.
     */
    case "cosh_" => env.acosh
    /*
    @s (a >NUM)' -> NUM'
    Hyperbolic arctangent of `a`.
     */
    case "tanh_" => env.atanh
    /*
    @s (a >NUM)' (b >NUM)' -> NUM'
    Base `b` logarithm of `a`.
     */
    case "log" => env.log
    /*
    @s (a >NUM)' -> NUM'
    Natural logarithm of `a`.
     */
    case "ln" => env.ln
    /*
    @s (a >NUM)' -> NUM'
    Base-10 logarithm of `a`.
     */
    case "logX" => env.log10
    /*
    @s (a >NUM)' -> NUM'
    Whether `a` is prime. Uses a strong pseudo-primality test with a 1/1e12 chance of being wrong.
     */
    case "P?" => env.isPrime
    /*
    @s (a >NUM)' -> MAP[(NUM => env.NUM)*]
    Prime-factorizes `a` into pairs of prime `y` and frequency `z`.
    ```sclin
    340P/
    ```
     */
    case "P/" => env.factor

    /*
    @s (a >TF)' -> TF'
    Atomic #{!`}.
     */
    case "!" => env.not
    /*
    @s (a >TF) -> TF
    Logical NOT.
     */
    case "!`" => env.not$$
    /*
    @s a' b' -> (a | b)'
    Atomic #{&`}.
     */
    case "&" => env.min
    /*
    @s (a >TF)' (b >TF)' -> TF'
    Atomic #{&&`}.
     */
    case "&&" => env.and
    /*
    @s a b -> a | b
    Minimum of `a` and `b`.
     */
    case "&`" => env.min$$
    /*
    @s (a >TF) (b >TF) -> TF
    Logical AND of `a` and `b`.
     */
    case "&&`" => env.and$$
    /*
    @s a' b' -> (a | b)'
    Atomic #{|`}.
     */
    case "|" => env.max
    /*
    @s (a >TF)' (b >TF)' -> TF'
    Atomic #{||`}.
     */
    case "||" => env.or
    /*
    @s a b -> a | b
    Maximum of `a` and `b`.
     */
    case "|`" => env.max$$
    /*
    @s (a >TF) (b >TF) -> TF
    Logical OR of `a` and `b`.
     */
    case "||`" => env.or$$
    /*
    @s a' b' -> (-1 | 0 | 1)'
    Atomic #{<=>`}.
     */
    case "<=>" => env.cmp
    /*
    @s a b -> -1 | 0 | 1
    Comparison (-1, 0, or 1 depending on whether `a` is less than, equal to, or greater than `b`).
     */
    case "<=>`" => env.cmp$$
    /*
    @s a' b' -> TF'
    Atomic #{=`}.
     */
    case "=" => env.eql
    /*
    @s a b -> TF
    Whether `a` loosely equals `b`.
     */
    case "=`" => env.eql$$
    /*
    @s a' b' -> TF'
    Atomic #{==`}.
     */
    case "==" => env.eqls
    /*
    @s a b -> TF
    Whether `a` strictly equals `b`.
     */
    case "==`" => env.eql$$
    /*
    @s a' b' -> TF'
    Atomic #{!=`}.
     */
    case "!=" => env.neq
    /*
    @s a b -> TF
    Whether `a` does not loosely equal `b`.
     */
    case "!=`" => env.neq$$
    /*
    @s a' b' -> TF'
    Atomic #{!=`}.
     */
    case "!==" => env.neqs
    /*
    @s a b -> TF
    Whether `a` does not loosely equal `b`.
     */
    case "!==`" => env.neqs$$
    /*
    @s a' b' -> TF'
    Atomic #{<`}.
     */
    case "<" => env.lt
    /*
    @s a b -> TF
    Whether `a` is less than `b`.
     */
    case "<`" => env.lt$$
    /*
    @s a' b' -> TF'
    Atomic #{>`}.
     */
    case ">" => env.gt
    /*
    @s a b -> TF
    Whether `a` is greater than `b`.
     */
    case ">`" => env.gt$$
    /*
    @s a' b' -> TF'
    Atomic #{<=`}.
     */
    case "<=" => env.lteq
    /*
    @s a b -> TF
    Whether `a` is less than or equal to `b`.
     */
    case "<=`" => env.gt$$
    /*
    @s a' b' -> TF'
    Atomic #{>=`}.
     */
    case ">=" => env.gteq
    /*
    @s a b -> TF
    Whether `a` is greater than or equal to `b`.
     */
    case ">=`" => env.gt$$

    /*
    @s (a >NUM)' -> NUM'
    Bitwise NOT.
     */
    case "b~" => env.bNOT
    /*
    @s (a >NUM)' (b >NUM)' -> NUM'
    Bitwise AND.
     */
    case "b&" => env.bAND
    /*
    @s (a >NUM)' (b >NUM)' -> NUM'
    Bitwise OR.
     */
    case "b|" => env.bOR
    /*
    @s (a >NUM)' (b >NUM)' -> NUM'
    Bitwise XOR.
     */
    case "b^" => env.bXOR
    /*
    @s (a >NUM)' (b >NUM)' -> NUM'
    Bitwise LSHIFT.
     */
    case "b<<" => env.bLSH
    /*
    @s (a >NUM)' (b >NUM)' -> NUM'
    Bitwise RSHIFT.
     */
    case "b>>" => env.bRSH
    /*
    @s (a >NUM)' (b >NUM)' -> NUM'
    Bitwise unsigned RSHIFT.
     */
    case "b>>>" => env.bURSH

    /*
    @s a i' -> (a._ | UN)'
    Value at atomic index `i` in `a`.
     */
    case ":" => env.get
    /*
    @s a -> a._
    Value at random index in `a`.
     */
    case ":r" => env.getr
    /*
    @s a i -> a._ | UN
    Value at index `i` in `a`.
     */
    case ":`" => env.get$$
    /*
    @s a (i >MAP) -> x
    #{:`} with `i` mapped over `a`.
     */
    case ":*" => env.gets
    /*
    @s a b (i >SEQ) -> x
    #{:`} with `i` folded over `a`.
     */
    case ":/" => env.getn
    /*
    @s a b i -> x
    Sets value at index `i` in `a` to `b`.
     */
    case ":=" => env.set
    /*
    @s a (m >MAP) -> x
    #{:=} with `i` mapped over `a`.
     */
    case ":*=" => env.sets
    /*
    @s a b (i >SEQ) -> x
    #{:=} with `i` folded over `a`.
     */
    case ":/=" => env.setn
    /*
    @s a (f >FN) i -> x
    Modifies value at index `i` using `f`.
     */
    case ":%" => env.setmod
    /*
    @s a (m >MAP[(_ => env.(_ >FN))*]) -> x
    #{:%} with `i` mapped over `a`.
     */
    case ":*%" => env.setmods
    /*
    @s a (f >FN) (i >SEQ) -> x
    #{:%} with `i` folded over `a`.
     */
    case ":/%" => env.setmodn
    /*
    @s a i -> x
    Removes index `i` from `a`.
     */
    case ":-" => env.idel
    /*
    @s a b' -> TF'
    Whether `a` has atomic `b`.
     */
    case ":?" => env.has
    /*
    @s a b -> TF
    Whether `a` has `b`.
    `MAP`s check `b` against keys; other types of `a` check `b` against values.
     */
    case ":?`" => env.has$$
    /*
    @s a -> NUM
    Length of `a`.
     */
    case "len" => env.len
    // TODO: docs
    case "~len" => env.olen
    /*
    @s a -> ARR[NUM]
    Shape of `a`, i.e. #{len} of each dimension of `a`.
    Determined by first element of each dimension.
     */
    case "shape" => env.shape
    /*
    @s a -> ARR[NUM]
    #{shape} but recursively maximizes lengths and depth
    instead of just using the first element.
     */
    case "shape^" => env.dshape
    case "shape=" => env.reshape
    /*
    @s a -> NUM
    #{len} of #{shape} of `a`.
     */
    case "rank" => env.rank
    /*
    @s a -> NUM
    #{len} of #{shape^} of `a`.
     */
    case "rank^" => env.depth
    /*
    @s a b -> ARR[a b]
    Pairs `a` and `b` in an `ARR`.
     */
    case "," => env.wrap$
    /*
    @s a -> ARR[a]
    Wraps `a` in an `ARR`.
     */
    case ",," => env.wrap
    /*
    @s a' b' -> ARR[a b]'
    Vectorized #{,}.
     */
    case ",'" => env.wrapv$
    /*
    @s a' -> ARR[a]'
    Vectorized #{,,}.
     */
    case ",,'" => env.wrapv
    /*
    @s a* -> a
    Wraps stack in an `ARR`.
     */
    case ",`" => env.wrap$$
    /*
    @s a -> a*
    Unwraps `a`.
     */
    case ",_" => env.unwrap
    /*
    @s _* a -> a*
    Replaces stack with `a` unwrapped.
     */
    case ",,_" => env.unwrap$
    /*
    @s a (n >NUM)' -> _
    Takes up to `n` items from `a`.
    Negative `n` takes from the end instead of the start.
     */
    case "tk" => env.tk
    /*
    @s a (n >NUM)' -> _
    Drops up to `n` items from `a`.
    Negative `n` drops from the end instead of the start.
     */
    case "dp" => env.dp
    /*
    @s a -> _
    Flattens `a` by one depth.
     */
    case "flat" => env.flat
    // TODO: docs
    case "~flat" => env.merge
    /*
    @s a -> _
    Flattens `a` recursively.
     */
    case "rflat" => env.rflat
    /*
    @s a -> SEQ
    Infinite `SEQ` with `a` repeated.
    ```sclin
    5rep 10tk >A
    ```
    ```sclin
    [1 2 3] rep 10tk >A
    ```
     */
    case "rep" => env.rep
    // TODO: docs
    case "~rep" => env.orep
    /*
    @s a -> SEQ
    Infinite `SEQ` with elements of `a` cycled.
    ```sclin
    [1 2 3] cyc 10tk >A
    ```
     */
    case "cyc" => env.cyc
    // TODO: docs
    case "~cyc" => env.ocyc
    /*
    @s (a >NUM)' -> ARR[1*]'
    Length-`a` `ARR` of 1's.
    ```sclin
    10I*
    ```
     */
    case "I*" => env.ones
    /*
    @s (a >ARR) -> ARR
    `ARR` of 1's with dimensions `a`.
    ```sclin
    [2 3 4] I^
    ```
     */
    case "I^" => env.one$
    /*
    @s a b -> _
    Convert the shape of `a` to the shape of `b`.
    ```sclin
    $W [2 3 4] I^ mold
    ```
    ```sclin
    $W [1 2 3] I* mold
    ```
     */
    case "mold" => env.toShape
    /*
    @s a (f: b -> _) -> SEQ
    Infinite `SEQ` of `f` successively #{Q}ed to `a`.
    ```sclin
    1 1.+ itr 10tk >A
    ```
    ```sclin
    1 ( 1+ 1 swap / ) itr 10tk >A
    ```
     */
    case "itr" => env.itr
    /*
    @s a (f: b -> _ _ | ) -> SEQ
    `SEQ` generated from `f` successively #{Q}ed to `a`,
    where `x` is the new current item and `y` is the next `b` to be subsequently #{Q}ed to `f`.
    Generation stops if `f` #{Q}ed to `a` results in an empty stack.
    ```sclin
    0 1, ( ,_ tuck + dups \swap dip ) fold_ 10tk >A
    ```
     */
    case "fold_" => env.unfold
    /*
    @s a -> (SEQ | ARR)[ARR[k v]*]
    `SEQ` of key/value pairs in `a`.
    ```sclin
    ["a" "b" "c" "d"] >kv >A
    ```
    ```sclin
    ["x""a", "y""b", "z""c", ]: >kv >A
    ```
     */
    case ">kv" => env.enumL
    /*
    @s a -> MAP
    #{>kv} and #{>M}.
    ```sclin
    ["a" "b" "c" "d"] =>kv
    ```
     */
    case "=>kv" => env.enumL.envMAP
    /*
    @s a -> SEQ | ARR
    Keys in `a`.
    ```sclin
    ["x" "a", "y" "b", "z" "c", ]: >k >A
    ```
     */
    case ">k" => env.keys
    /*
    @s a -> SEQ | ARR
    Values in `a`.
    ```sclin
    ["x""a", "y""b", "z""c", ]: >v >A
    ```
     */
    case ">v" => env.vals
    /*
    @s (a >NUM)' (b >NUM)' -> ARR[NUM*]'
    Exclusive range from `a` to `b`.
     */
    case "a>b" => env.range
    /*
    @s (a >NUM)' (b >NUM)' -> ARR[NUM*]'
    Inclusive range from `a` to `b`.
     */
    case "a-b" => env.irange
    /*
    @s (a >NUM)' -> ARR[NUM*]'
    Exclusive range from 0 to `a`.
     */
    case "O>a" => env.push(NUM(0)).swap.range
    /*
    @s (a >NUM)' -> ARR[NUM*]'
    Inclusive range from 0 to `a`.
     */
    case "O-a" => env.push(NUM(0)).swap.irange
    /*
    @s (a >NUM)' -> ARR[NUM*]'
    Exclusive range from `a` to 0.
     */
    case "a>O" => env.push(NUM(0)).range
    /*
    @s (a >NUM)' -> ARR[NUM*]'
    Inclusive range from `a` to 0.
     */
    case "a-O" => env.push(NUM(0)).irange
    /*
    @s (a >NUM)' -> ARR[NUM*]'
    Exclusive range from 1 to `a`.
     */
    case "I>a" => env.push(NUM(1)).swap.range
    /*
    @s (a >NUM)' -> ARR[NUM*]'
    Inclusive range from 1 to `a`.
     */
    case "I-a" => env.push(NUM(1)).swap.irange
    /*
    @s (a >NUM)' -> ARR[NUM*]'
    Exclusive range from `a` to 1.
     */
    case "a>I" => env.push(NUM(1)).range
    /*
    @s (a >NUM)' -> ARR[NUM*]'
    Inclusive range from `a` to 1.
     */
    case "a-I" => env.push(NUM(1)).irange
    /*
    @s a -> _
    Shuffles `a`.
    ```sclin
    10O>a shuf
    ```
     */
    case "shuf" => env.shuffle
    /*
    @s a -> SEQ
    All permutations of `a`.
    ```sclin
    [1 2 3] perm >A
    ```
     */
    case "perm" => env.perm
    /*
    @s a (n >NUM)' -> SEQ'
    All length-`n` combinations of `a`.
    ```sclin
    [1 2 3] 2comb >A
    ```
     */
    case "comb" => env.comb
    /*
    @s a -> SEQ
    All subsets of `a`.
    ```sclin
    [1 2 3] ^set >A
    ```
     */
    case "^set" => env.powset
    /*
    @s a[_*] -> SEQ'
    Cartesian product of iterable-of-iterables `a` to `n`.
    ```sclin
    ["abc" "def" "ghi"] Q* >A
    ```
     */
    case "Q*" => env.cProd
    /*
    @s a[_*] -> _[_*]
    Transposes a collection of collections matrix-style.
    Safe for infinite lists.
    ```sclin
    [[1 2 3][4 5 6][7 8 9]] tpose
    ```
    ```sclin
    [[1 2][3 4 5][6]] tpose
    ```
     */
    case "tpose" => env.transpose
    // TODO: docs
    case "paxes"  => env.paxes
    case "paxes~" => env.paxes$
    /*
    @s (a >STR)' (b >NUM)' (c >STR)' -> STR'
    Atomic #{pad`}.
     */
    case "pad" => env.pad
    /*
    @s (a >STR)' (b >NUM)' (c >STR)' -> STR'
    Atomic #{padl`}.
     */
    case "padl" => env.padl
    /*
    @s (a >STR)' (b >NUM)' (c >STR)' -> STR'
    Atomic #{padc`}.
     */
    case "padc" => env.padc
    /*
    @s a[_*] (b >NUM)' c -> STR'
    Pads `a` from the right to length `b` using `c`.
    ```sclin
    [1 2 3 4] 9 0pad`
    ```
    ```sclin
    [1 2 3 4] 9 [5 6 7] pad`
    ```
    ```sclin
    [1 2 3 4] 3 0pad`
    ```
     */
    case "pad`" => env.pad$
    /*
    @s a[_*] (b >NUM)' c -> STR'
    Pads `a` from the right to length `b` using `c`.
    ```sclin
    [1 2 3 4] 9 0padl`
    ```
    ```sclin
    [1 2 3 4] 9 [5 6 7] padl`
    ```
    ```sclin
    [1 2 3 4] 3 0padl`
    ```
     */
    case "padl`" => env.padl$
    /*
    @s a[_*] (b >NUM)' c -> STR'
    Pads `a` from the right to length `b` using `c`.
    ```sclin
    [1 2 3 4] 9 0padc`
    ```
    ```sclin
    [1 2 3 4] 9 [5 6 7] padc`
    ```
    ```sclin
    [1 2 3 4] 3 0padc`
    ```
     */
    case "padc`" => env.padc$

    /*
    @s (a >STR)' -> ARR[NUM*]'
    Converts `a` to codepoints.
    ```sclin
    "hello"S>c
    ```
     */
    case "S>c" => env.toCodePt
    /*
    @s (a >ARR[NUM*]) -> STR
    Converts iterable of codepoints to `STR`.
    ```sclin
    [104 101 108 108 111] c>S
    ```
     */
    case "c>S" => env.fromCodePt
    /*
    @s (a >STR)' (b >STR)' -> ARR'
    Splits `a` with `b`.
     */
    case "<>" => env.split
    /*
    @s a (i >NUM) -> ARR[_ _]
    #{tk} and #{dp} of `a` at index `i`.
     */
    case "<>:" => env.splitAt
    /*
    @s (a >STR)' -> ARR'
    #{<>}s with empty string.
     */
    case "c<>" => env.push(STR("")).split
    /*
    @s (a >STR)' -> ARR'
    #{<>}s with space.
     */
    case "w<>" => env.push(STR(" ")).split
    /*
    @s (a >STR)' -> ARR'
    #{<>}s with newline.
     */
    case "n<>" => env.push(STR("\n")).split
    /*
    @s (a >STR)' -> ARR'
    #{<>}s on whitespace characters.
     */
    case "s<>" => env.ssplit
    /*
    @s a (b >STR)' -> STR'
    Joins `a` with `b`.
     */
    case "><" => env.join
    /*
    @s a -> STR'
    #{><}s with empty string.
     */
    case "c><" => env.push(STR("")).join
    /*
    @s a -> STR'
    #{><}s with space.
     */
    case "w><" => env.push(STR(" ")).join
    /*
    @s a -> STR'
    #{><}s with newline.
     */
    case "n><" => env.push(STR("\n")).join
    /*
    @s (a >STR)' -> STR'
    Converts `STR` to `lowercase`.
     */
    case "A>a" => env.toLower
    /*
    @s (a >STR)' -> STR'
    Converts `STR` to `UPPERCASE`.
     */
    case "a>A" => env.toUpper
    /*
    @s (a >STR)' -> STR'
    Converts `STR` to `Capitalized`.
     */
    case ">Aa" => env.toCap
    /*
    @s (a >STR)' (r >STR)' -> SEQ[MAP]'
    Matches `a` with regex `r`.
    Each match returned is a `MAP` with the following keys:
    - ``` & ```: Matched `STR`.
    - ``` ` ```: `STR` before the match.
    - ``` ' ```: `STR` after the match.
    - ``` * ```: `ARR[MAP]` of each capturing group matched.
    - ``` ^ ```: `NUM` index of the match's start.
    - ``` $ ```: `NUM` index of the match's end.
     */
    case "/?" => env.rmatch
    /*
    @s (a >STR)' (r >STR)' -> SEQ[STR]'
    #{/?} with only `&` keys.
     */
    case "/?&" => env.rmatchMatch
    /*
    @s (a >STR)' (r >STR)' -> SEQ[STR]'
    #{/?} with only `'` keys.
     */
    case "/?`" => env.rmatchBefore
    /*
    @s (a >STR)' (r >STR)' -> SEQ[STR]'
    #{/?} with only ``` ` ``` keys.
     */
    case "/?'" => env.rmatchAfter
    /*
    @s (a >STR)' (r >STR)' -> SEQ[ARR[MAP]]'
    #{/?} with only `*` keys.
     */
    case "/?*" => env.rmatchGroups
    /*
    @s (a >STR)' (b >STR)' -> SEQ[NUM]'
    #{/?} with only `^` keys.
     */
    case "/?^" => env.rmatchStart
    /*
    @s (a >STR)' (b >STR)' -> SEQ[NUM]'
    #{/?} with only `$` keys.
     */
    case "/?$" => env.rmatchEnd
    /*
    @s (a >STR)' (r >STR)' (f: MAP -> >STR)' -> STR'
    Replace matches of regex `r` on `a` by applying each match `MAP` to `f`.
     */
    case "/#" => env.rsub
    /*
    @s (a >STR)' (r >STR)' (s >STR)' -> STR'
    Replace first match of regex `r` on `a` with `s`.
     */
    case "/#^" => env.rsubFirst

    /*
    @s a f' -> _'
    #{Q}s `f` on each element of `a`.
    If `a` is `MAP`, then the signature of `f` is `k v -> _`,
    where `k=>v` env.is the key-value pair.
    Otherwise, the signature of `f` is `x -> _`,
    where `x` is the element.
    ```sclin
    [1 2 3 4] 1.+ map
    ```
    ```sclin
    [0 1, 2 3, 4 5, ]: ( over + ) map
    ```
     */
    case "map" => env.map
    // TODO: docs
    case "~map" => env.mapEval
    /*
    @s a f' -> a
    #{map} but `a` is preserved (i.e. leaving only side effects of `f`).
    ```sclin
    [1 2 3 4] ( 1+ n>o ) tap
    ```
     */
    case "tap" => env.tapMap
    /*
    @s a f' -> _'
    #{map} and #{flat}.
    ```sclin
    1224P/ \*` mapf
    ```
     */
    case "mapf" => env.flatMap
    // TODO: docs
    case "~mapf" => env.mergeMap
    /*
    @s a f' (n >NUM)' -> _'
    `n`-wise reduction of `f` over `a`.
    ```sclin
    [1 2 3 4] \+ 2%map
    ```
     */
    case "%map" => env.winMap
    /*
    @s a b (f: x y -> _)' -> _'
    #{Q}s `f` over each element-wise pair of `a` and `b`.
    Iterables of differing length truncate to the shorter length when zipped.
    ```sclin
    [1 2 3 4] [2 3 4 5] \, zip
    ```
    ```sclin
    [1 2 3 4] [2 3] \+ zip
    ```
    ```sclin
    [1 2 3 4] [1 "a", 3 "b", "x" "c", ]: \, zip
    ```
     */
    case "zip" => env.zip
    /*
    @s a b c d (f: x y -> _)' -> _'
    #{zip} but instead of truncating,
    uses `c` and `d` as fill elements for `a` and `b` respectively.
    ```sclin
    [1 2 3 4] [2 3 4 5] UN UN \, zip~
    ```
    ```sclin
    [1 2 3 4] [2 3] UN UN \+ zip~
    ```
    ```sclin
    [1 2 3 4] [1 "a", 3 "b", "x" "c", ]: UN UN \, zip~
    ```
     */
    case "zip~" => env.zip$
    /*
    @s a b (f: x y -> _)' -> _'
    #{Q}s `f` over each table-wise pair of `a` and `b`.
    ```sclin
    [1 2 3 4] [2 3 4 5] \++ tbl
    ```
     */
    case "tbl" => env.tbl
    /*
    @s a b (f: x y -> _)' -> _'
    #{tbl} and #{flat}.
     */
    case "tblf" => env.tblf
    /*
    @s a f' -> _'
    Atomic/recursive #{map}.
    ```sclin
    [[1 2] 3 4 [5 [6 7]]] ( dup n>o ) rmap
    ```
     */
    case "rmap" => env.rmap
    // TODO: docs
    case "dmap" => env.dmap
    /*
    @s a b f' -> _'
    #{Q}s `f` to combine each accumulator and element starting from initial accumulator `b`.
    If `a` is `MAP`, then the signature of `f` is `k x v -> _`,
    where `k=>v` is the key-value pair and `x` is the accumulator.
    Otherwise, the signature of `f` is `x y -> _`,
    where `x` is the accumulator and `y` is the value.
    ```sclin
    [1 2 3 4] 0 \+ fold
    ```
    ```sclin
    "1011"_` =>kv 0 ( rot 2 swap ^ * + ) fold
    ```
     */
    case "fold" => env.fold
    // TODO: docs
    case "~fold" => env.ofold
    /*
    @s a b f' -> _'
    #{fold} from the right.
     */
    case "foldr" => env.foldR
    /*
    @s a b f' -> _'
    Atomic/recursive #{fold}.
    ```sclin
    [[1 2] 3 4 [5 [6 7]]] 0 \+ rfold
    ```
    ```sclin
    [[1 2] 3 4 [5 [6 7]]] [] \+` rfold
    ```
     */
    case "rfold" => env.rfold
    /*
    @s a b f' -> _'
    #{rfold} from the right.
     */
    case "rfoldr" => env.rfoldR
    /*
    @s a f' -> _'
    #{fold} without initial accumulator, instead using the first element of `a`.
    If `a` is empty, then an error is thrown.
    ```sclin
    [1 2 3 4] \+ fold~
    ```
    ```sclin
    [1 5 10 4 3] \| fold~
    ```
     */
    case "fold~" => env.reduce
    /*
    @s a f' -> _'
    #{fold~} from the right.
     */
    case "foldr~" => env.reduceR
    /*
    @s a b f' -> _'
    #{fold} with intermediate values.
    ```sclin
    [1 2 3 4] 0 \+ scan
    ```
     */
    case "scan" => env.scan
    // TODO: docs
    case "~scan" => env.scanEval
    /*
    @s a b f' -> _'
    #{scan} from the right.
     */
    case "scanR" => env.scanR
    /*
    @s a -> NUM
    Sum of `a`. Equivalent to `0 \+ rfold`.
     */
    case "+/" => env.push(NUM(0)).push(CMD("+")).rfold
    /*
    @s a -> NUM
    Product of `a`. Equivalent to `1 \* rfold`.
     */
    case "*/" => env.push(NUM(1)).push(CMD("*")).rfold
    /*
    @s a -> _
    Minimum of `a`. Equivalent to ``` \&` fold~ ```.
     */
    case "&/" => env.push(CMD("&`")).reduce
    /*
    @s a -> _
    Maximum of `a`. Equivalent to ``` \|` fold~ ```.
     */
    case "|/" => env.push(CMD("|`")).reduce
    // TODO: this doc sucks
    /*
    @s a f' -> _'
    A multi-purpose function for creating, modifying, and traversing nested structures.
    ```sclin
    [[1 2] 3 4 [ "a" 5, "b" [6 7] , ]: ] ( dups f>o ) walk
    ```
    ```sclin
    [[1 2] 3 4 [ "a" 5, "b" [6 7] , ]: ] ( dup len 0> ( dup +` ) &# ) walk
    ```
     */
    case "walk" => env.walk
    /*
    @s a f' -> _'
    Keeps elements of `a` that satisfy predicate `f`.
    If `a` is `MAP`, then the signature of `f` is `k v -> >TF`,
    where `k=>v` is the key-value pair.
    Otherwise, the signature of `f` is `x -> >TF`,
    where `x` is the element.
    ```sclin
    [5 1 2 4 3] 2.> fltr
    ```
     */
    case "fltr" => env.fltr
    /*
    @s a f' -> TF'
    Whether any elements of `a` satisfy predicate `f`.
    See #{fltr} for the signature of `f`.
    ```sclin
    [5 1 2 4 3] 2.> any
    ```
     */
    case "any" => env.any
    // TODO: docs
    case "~any" => env.oany
    /*
    @s a f' -> TF'
    Whether all elements of `a` satisfy predicate `f`.
    See #{fltr} for the signature of `f`.
    ```sclin
    [5 1 2 4 3] 2.> all
    ```
     */
    case "all" => env.all
    // TODO: docs
    case "~all" => env.all
    /*
    @s a f' -> _'
    Takes elements of `a` until #{Q}ing `f` is falsy.
    See #{fltr} for the signature of `f`.
    ```sclin
    [5 1 2 4 3] 4.!= tk*
    ```
     */
    case "tk*" => env.tkwl
    /*
    @s a f' -> _'
    Drops elements of `a` while predicate `f` is truthy.
    See #{fltr} for the signature of `f`.
    ```sclin
    [5 1 2 4 3] 4.!= dp*
    ```
     */
    case "dp*" => env.dpwl
    /*
    @s a f' -> _'
    Finds first element of `a` where predicate `f` is truthy.
    See #{fltr} for the signature of `f`.
    Returns `UN` if not found.
    ```sclin
    [5 1 2 4 3] ( 2% ! ) find
    ```
     */
    case "find" => env.find
    // TODO: docs
    case "~find" => env.ofind
    /*
    @s a f' -> NUM'
    Finds index of first element of `a` where predicate `f` is truthy.
    See #{fltr} for the signature of `f`.
    Returns `-1` if not found.
    ```sclin
    [5 1 2 4 3] ( 2% ! ) find:
    ```
     */
    case "find:" => env.findi
    /*
    @s a f' -> _'
    Deletes first element of `a` where predicate `f` is truthy.
    See #{fltr} for the signature of `f`.
    ```sclin
    [5 1 2 4 3] ( 2% ! ) del
    ```
     */
    case "del" => env.del
    /*
    @s a f' -> _'
    Uniquifies elements of `a` with mapper `f`.
    See #{map} for the signature of `f`.
    ```sclin
    [2 4 3 3 5 4 1] () uniq
    ```
    ```sclin
    [5 1 2 4 3] 2.% uniq
    ```
     */
    case "uniq" => env.uniq
    /*
    @s a f' -> _'
    Uniquifies elements of `a` with comparator `f`.
    See #{sort~} for the signature of `f`.
    ```sclin
    [2 4 3 3 5 4 1] \=` uniq~
    ```
    ```sclin
    [2 4 3 3 5 4 1] 2.% uniq~
    ```
     */
    case "uniq~" => env.uniq$
    /*
    @s a f' -> _'
    Sorts elements of `a` with mapper `f`.
    See #{map} for the signature of `f`.
    ```sclin
    ["a" "" "abc" "ab"] \len sort
    ```
    ```sclin
    [1 2 3 4 5] \$rng sort
    ```
     */
    case "sort" => env.sort
    /*
    @s a f' -> _'
    Sorts elements of `a` with comparator `f`.
    If `a` is `MAP`, then the signature of `f` is `ARR[k v] ARR[j w] -> >TF`,
    where `k=>v` and `j=>w` are key-value pairs to compare.
    Otherwise, the signature of `f` is `x y -> >TF`,
    where `x` and `y` are elements to compare.
    ```sclin
    [1 5 2 3 4] \< sort~
    ```
    ```sclin
    [1 5 2 3 4] \> sort~
    ```
     */
    case "sort~" => env.sort$
    /*
    @s a f' -> ARR[_ _]'
    Separates `a` into 2 parts based on predicate `f`.
    See #{fltr} for the signature of `f`.
    ```sclin
    [5 1 2 4 3] 2.> part
    ```
     */
    case "part" => env.part
    /*
    @s a f' -> MAP'
    Separates `a` groups based on `f`.
    Each result of `f` becomes a key in the resulting `MAP`.
    See #{map} for the signature of `f`.
    ```sclin
    "abc"^set >A \len group
    ```
     */
    case "group" => env.group
    // TODO: docs
    case "~group" => env.ogroup
    /*
    @s a f' -> ARR[_ _]'
    Equivalent to a combination of #{tk*} and #{dp*}.
    See #{fltr} for the signature of `f`.
    ```sclin
    [5 1 2 4 3] 2.% span
    ```
     */
    case "span" => env.span
    /*
    @s a f' -> _'
    Groups consecutive duplicate runs of `a` based on predicate `f`.
    See #{sort~} for the signature of `f`.
    ```sclin
    [1 1 2 3 3 4 6 4 4] \=` pack
    ```
     */
    case "pack" => env.pack

    /*
    @s a b f' -> _'
    Gets the union of `a` and `b` with comparator `f`.
    See #{sort~} for the signature of `f`.
    ```sclin
    [1 2 3 4] [2 4 6 8] \=` union
    ```
     */
    case "union" => env.union
    /*
    @s a b f' -> _'
    Gets the intersection between `a` and `b` with comparator `f`.
    May hang if `a` or `b` are infinite.
    See #{sort~} for the signature of `f`.
    ```sclin
    [1 2 3 4] [2 4 6 8] \=` intxn
    ```
     */
    case "intxn" => env.intersect
    /*
    @s a b f' -> _'
    Gets the difference between `a` and `b` with comparator `f`.
    Will hang if `b` is infinite.
    See #{sort~} for the signature of `f`.
    ```sclin
    [1 2 3 4] [2 4 6 8] \=` diff
    ```
     */
    case "diff" => env.diff

    /*
    @s (a >FUT[x])' -> x'
    Synchronously waits for `a` to complete, leaving the result on the stack.
     */
    case "~_" => env.await
    /*
    @s (a >FUT[x])' -> TRY[x]'
    #{~_} with result wrapped in a `TRY`.
     */
    case "~_!" => env.awaitTRY
    /*
    @s (a >FUT)' ->
    Cancels `a`.
     */
    case "~$" => env.cancelFUT
    /*
    @s a[>TASK*] -> TASK[_[_*]]
    Executes each `TASK` in `a` sequentially such that both effects and results are ordered.
     */
    case "~|>" => env.seqTASK
    /*
    @s a[>TASK*] -> TASK[_[_*]]
    Executes each `TASK` in `a` in parallel such that effects are unordered but results are ordered.
     */
    case "~||" => env.parTASK
    /*
    @s a[>TASK*] (n >NUM) -> TASK[_[_*]]
    #{~||} but with at most `n` concurrently running `TASK`s.
     */
    case "~||>" => env.parTASK
    /*
    @s a[>TASK*] -> TASK[_[_*]]
    #{~||} but results are also unordered.
     */
    case "~//" => env.parunTASK
    /*
    @s a[>TASK*] -> TASK
    Races a collection of `TASK`s, returning the first to complete.
     */
    case "~>>" => env.raceTASK
    /*
    @s (a >TASK)' -> TASK'
    Ensures that `a` runs on a separate thread.
     */
    case "~<" => env.forkTASK
    /*
    @s (a >TASK)' -> TASK'
    Ensures that `a` is memoized such that subsequent runs of the task return the same value.
     */
    case "~:" => env.memoTASK
    /*
    @s (a >TASK)' -> TASK'
    #{~:} but only if `a` completes successfully.
     */
    case "~:&" => env.memoTASK$
    /*
    @s (a >TASK)' -> TASK'
    Ensures that `a` is uncancellable.
     */
    case "~$_" => env.uncancelTASK
    /*
    @s (a >TASK)' (n >NUM)' -> TASK'
    Ensures that `a` will error if not completed within `n` milliseconds.
     */
    case "~%" => env.timeoutTASK
    /*
    @s (n >NUM)' -> TASK[n]'
    Creates an asynchronous `TASK` that will complete after `n` milliseconds.
     */
    case "sleep" => env.sleep

    case _ => throw LinEx("FN", s"unknown fn \"$x\"")

    // CMDOC END
