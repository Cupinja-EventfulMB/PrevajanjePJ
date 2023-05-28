package task
import java.io.InputStream
import java.io.File

const val ERROR_STATE = 0

const val EOF_SYMBOL = -1
const val SKIP_SYMBOL = 0
const val NEWLINE = '\n'.code
const val INT = 1
const val VAR = 2
const val PLUS = 3
const val MINUS = 4
const val LPAREN = 5
const val RPAREN = 6
const val LSPAREN = 7
const val RSPAREN = 8
const val LCPAREN = 9
const val RCPAREN = 10
const val ASSIGN = 11
const val CITY = 12
const val STREET = 13
const val POINT = 14
const val INSTITUTION = 15
const val SQUARE = 16
const val STATUE = 17
const val STRING = 18
const val BLOCK = 19
const val BEND = 20
const val LINE = 21
const val COMMA = 22
const val ADDRESS = 23
const val DEC_STRING = 24
const val DEC_INT = 25
const val DEC_COORD = 26
const val EVENTS = 27
const val FST = 28
const val SND = 29
const val CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
const val STRING_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ 0123456789.,"
const val NUMBER_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"


interface DFA {
    val states: Set<Int>
    val alphabet: IntRange
    fun next(state: Int, code: Int): Int
    fun symbol(state: Int): Int
    val startState: Int
    val finalStates: Set<Int>
}

object ForForeachFFFAutomaton: DFA {
    override val states = (1 .. 96).toSet()
    override val alphabet = 0 .. 255
    override val startState = 1
    override val finalStates = setOf(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 17, 23, 28, 39, 44, 48, 54, 57, 61, 63, 64, 71, 74, 75, 84, 89, 92, 94, 95, 96)

    private val numberOfStates = states.max() + 1 // plus the ERROR_STATE
    private val numberOfCodes = alphabet.max() + 1 // plus the EOF
    private val transitions = Array(numberOfStates) {IntArray(numberOfCodes)}
    private val values = Array(numberOfStates) {SKIP_SYMBOL}

    private fun setTransition(from: Int, chr: Char, to: Int) {
        transitions[from][chr.code + 1] = to // + 1 because EOF is -1 and the array starts at 0
    }

    private fun setTransition(from: Int, code: Int, to: Int) {
        transitions[from][code + 1] = to
    }

    private fun setSymbol(state: Int, symbol: Int) {
        values[state] = symbol
    }

    override fun next(state: Int, code: Int): Int {
        assert(states.contains(state))
        assert(alphabet.contains(code))
        return transitions[state][code + 1]
    }

    override fun symbol(state: Int): Int {
        assert(states.contains(state))
        return values[state]
    }
    init {
        // int [0-9]+
        for (a in '0' .. '9'){
            setTransition(1,a,2)
            setTransition(2,a,2)
        }
        setSymbol(2,INT)

        // variable [a-zA-Z]+[0-9]*
        for(character in CHARS){
            setTransition(1, character, 3)
        }
        for(character in CHARS){
            setTransition(3, character, 3)
        }
        for(digit in '0'..'9'){
            setTransition(3, digit, 4)
        }
        for(digit in '0'..'9'){
            setTransition(4, digit, 4)
        }
        setSymbol(3, VAR)
        setSymbol(4,VAR)

        // plus
        setTransition(1,'+',5)
        setSymbol(5,PLUS)

        // minus
        setTransition(1,'-',6)
        setSymbol(6,MINUS)

        // lparen
        setTransition(1,'(',7)
        setSymbol(7,LPAREN)

        // rparen
        setTransition(1,')',8)
        setSymbol(8,RPAREN)

        // lsparen
        setTransition(1,'[',9)
        setSymbol(9,LSPAREN)

        // rsparen
        setTransition(1,']',10)
        setSymbol(10,RSPAREN)

        // lcparen
        setTransition(1,'{',11)
        setSymbol(11,LCPAREN)

        // rcparen
        setTransition(1,'}',12)
        setSymbol(12,RCPAREN)

        // assign
        setTransition(1,'=',13)
        setSymbol(13,ASSIGN)

        // City
        setTransition(1, 'c', 14)
        setTransition(14, 'i', 15)
        setTransition(15, 't', 16)
        setTransition(16, 'y', 17)

        for(character in CHARS){
            if ((character != 'i') && (character != 'o')) setTransition(14, character, 3)
            if (character != 't') setTransition(15, character, 3)
            if (character != 'y') setTransition(16, character, 3)
            if (character != ' ') setTransition(17, character, 3)
        }
        setSymbol(17, CITY)

        // Street
        setTransition(1, 's', 18)
        setTransition(18, 't', 19)
        setTransition(19, 'r', 20)
        setTransition(20, 'e', 21)
        setTransition(21, 'e', 22)
        setTransition(22, 't', 23)

        for(character in CHARS){
            if ((character != 't') && (character != 'q') && (character != 'n')) setTransition(18, character, 3)
            if ((character != 'r') && (character != 'a')) setTransition(19, character, 3)
            if ((character != 'e') && (character !='i')) setTransition(20, character, 3)
            if (character != 'e') setTransition(21, character, 3)
            if (character != 't') setTransition(22, character, 3)
            if (character != ' ') setTransition(23, character, 3)
        }
        setSymbol(23, STREET)

        // Point
        setTransition(1, 'p', 24)
        setTransition(24, 'o', 25)
        setTransition(25, 'i', 26)
        setTransition(26, 'n', 27)
        setTransition(27, 't', 28)

        for(character in CHARS){
            if (character != 'o') setTransition(24, character, 3)
            if (character != 'i') setTransition(25, character, 3)
            if (character != 'n') setTransition(26, character, 3)
            if (character != 't') setTransition(27, character, 3)
            if (character != ' ') setTransition(28, character, 3)
        }
        setSymbol(28, POINT)

        // Institution
        setTransition(1, 'i', 29)
        setTransition(29, 'n', 30)
        setTransition(30, 's', 31)
        setTransition(31, 't', 32)
        setTransition(32, 'i', 33)
        setTransition(33, 't', 34)
        setTransition(34, 'u', 35)
        setTransition(35, 't', 36)
        setTransition(36, 'i', 37)
        setTransition(37, 'o', 38)
        setTransition(38, 'n', 39)

        for(character in CHARS){
            if ((character != 'n') && (character != 't')) setTransition(29, character, 3)
            if (character != 's') setTransition(30, character, 3)
            if (character != 't') setTransition(31, character, 3)
            if (character != 'i') setTransition(32, character, 3)
            if (character != 't') setTransition(33, character, 3)
            if (character != 'u') setTransition(34, character, 3)
            if (character != 't') setTransition(35, character, 3)
            if (character != 'i') setTransition(36, character, 3)
            if (character != 'o') setTransition(37, character, 3)
            if (character != 'n') setTransition(38, character, 3)
            if (character != ' ') setTransition(39, character, 3)
        }
        setSymbol(39, INSTITUTION)

        // Square
        setTransition(18, 'q', 40)
        setTransition(40, 'u', 41)
        setTransition(41, 'a', 42)
        setTransition(42, 'r', 43)
        setTransition(43, 'e', 44)

        for(character in CHARS){
            if (character != 'u') setTransition(40, character, 3)
            if (character != 'a') setTransition(41, character, 3)
            if (character != 'r') setTransition(42, character, 3)
            if (character != 'e') setTransition(43, character, 3)
            if (character != ' ') setTransition(44, character, 3)
        }
        setSymbol(44, SQUARE)

        // Statue
        setTransition(19, 'a', 45)
        setTransition(45, 't', 46)
        setTransition(46, 'u', 47)
        setTransition(47, 'e', 48)

        for(character in CHARS){
            if (character != 't') setTransition(45, character, 3)
            if (character != 'u') setTransition(46, character, 3)
            if (character != 'e') setTransition(47, character, 3)
            if (character != ' ') setTransition(48, character, 3)
        }
        setSymbol(48, STATUE)

        // Block
        setTransition(1, 'b', 50)
        setTransition(50, 'l', 51)
        setTransition(51, 'o', 52)
        setTransition(52, 'c', 53)
        setTransition(53, 'k', 54)

        for(character in CHARS){
            if (character != 'l') setTransition(50, character, 3)
            if (character != 'o') setTransition(51, character, 3)
            if (character != 'c') setTransition(52, character, 3)
            if (character != 'k') setTransition(53, character, 3)
            if (character != ' ') setTransition(54, character, 3)
        }
        setSymbol(54, BLOCK)

        // Bend
        setTransition(50, 'e', 55)
        setTransition(55, 'n', 56)
        setTransition(56, 'd', 57)
        for(character in CHARS){
            if (character != 'n') setTransition(55, character, 3)
            if (character != 'd') setTransition(56, character, 3)
            if (character != ' ') setTransition(57, character, 3)
        }
        setSymbol(57, BEND)

        // Line
        setTransition(1, 'l', 58)
        setTransition(58, 'i', 59)
        setTransition(59, 'n', 60)
        setTransition(60, 'e', 61)

        for(character in CHARS){
            if (character != 'i') setTransition(58, character, 3)
            if (character != 'n') setTransition(59, character, 3)
            if (character != 'e') setTransition(60, character, 3)
            if (character != ' ') setTransition(61, character, 3)
        }
        setSymbol(61, LINE)

        //" element in STRING_CHARS "
        setTransition(1, '"', 62)
        for(character in STRING_CHARS){
            setTransition(62, character, 62)
        }
        setTransition(62,'"', 63)
        setSymbol(63, STRING)

        // comma
        setTransition(1,',',64)
        setSymbol(64, COMMA)

        // address
        setTransition(1, 'a', 65)
        setTransition(65, 'd', 66)
        setTransition(66, 'd', 67)
        setTransition(67, 'r', 68)
        setTransition(68, 'e', 69)
        setTransition(69, 's', 70)
        setTransition(70, 's', 71)

        for(character in CHARS){
            if (character != 'd') setTransition(65, character, 3)
            if (character != 'd') setTransition(66, character, 3)
            if (character != 'r') setTransition(67, character, 3)
            if (character != 'e') setTransition(68, character, 3)
            if (character != 's') setTransition(69, character, 3)
            if (character != 's') setTransition(70, character, 3)
            if (character != ' ') setTransition(71, character, 3)
        }
        setSymbol(71, ADDRESS)

        setTransition(20, 'i', 72)
        setTransition(72, 'n', 73)
        setTransition(73, 'g', 74)

        for(character in CHARS){
            if (character != 'n') setTransition(72, character, 3)
            if (character != 'g') setTransition(73, character, 3)
            if (character != ' ') setTransition(74, character, 3)
        }
        setSymbol(74, DEC_STRING)

        //int
        setTransition(30, 't', 75)

        for(character in CHARS){
            if (character != ' ') setTransition(75, character, 3)
        }
        setSymbol(75, DEC_INT)

        // Coordinate
        setTransition(14, 'o', 76)
        setTransition(76, 'o', 77)
        setTransition(77, 'r', 78)
        setTransition(78, 'd', 79)
        setTransition(79, 'i', 80)
        setTransition(80, 'n', 81)
        setTransition(81, 'a', 82)
        setTransition(82, 't', 83)
        setTransition(83, 'e', 84)

        for(character in CHARS){
            if (character != 'o') setTransition(76, character, 3)
            if (character != 'r') setTransition(77, character, 3)
            if (character != 'd') setTransition(78, character, 3)
            if (character != 'i') setTransition(79, character, 3)
            if (character != 'n') setTransition(80, character, 3)
            if (character != 'a') setTransition(81, character, 3)
            if (character != 't') setTransition(82, character, 3)
            if (character != 'e') setTransition(83, character, 3)
            if (character != ' ') setTransition(84, character, 3)
        }
        setSymbol(84, DEC_COORD)

        // events
        setTransition(1, 'e', 84)
        setTransition(84, 'v', 85)
        setTransition(85, 'e', 86)
        setTransition(86, 'n', 87)
        setTransition(87, 't', 88)
        setTransition(88, 's', 89)

        for(character in CHARS){
            if (character != 'v') setTransition(84, character, 3)
            if (character != 'e') setTransition(85, character, 3)
            if (character != 'n') setTransition(86, character, 3)
            if (character != 't') setTransition(87, character, 3)
            if (character != 's') setTransition(88, character, 3)
            if (character != ' ') setTransition(89, character, 3)
        }
        setSymbol(89, EVENTS)

        // fst
        setTransition(1, 'f', 90)
        setTransition(90, 's', 91)
        setTransition(91, 't', 92)

        for(character in CHARS){
            if (character != 's') setTransition(90, character, 3)
            if (character != 't') setTransition(91, character, 3)
            if (character != ' ') setTransition(92, character, 3)
        }
        setSymbol(92, FST)

        // snd
        setTransition(18, 'n', 93)
        setTransition(93, 'd', 94)

        for(character in CHARS){
            if (character != 'd') setTransition(93, character, 3)
            if (character != ' ') setTransition(94, character, 3)
        }
        setSymbol(94, SND)

        // ignore [\n\r\t ]+
        setTransition(1,'\n',95)
        setTransition(1,'\r',95)
        setTransition(1,'\t',95)
        setTransition(1,' ',95)
        setSymbol(95,SKIP_SYMBOL)

        // EOF
        setTransition(1,-1,96)
        setSymbol(96,EOF_SYMBOL)

    }
}

data class Token(val symbol: Int, val lexeme: String, val startRow: Int, val startColumn: Int)

class Scanner(private val automaton: DFA, private val stream: InputStream) {
    private var last: Int? = null
    private var row = 1
    private var column = 1

    private fun updatePosition(code: Int) {
        if (code == NEWLINE) {
            row += 1
            column = 1
        } else {
            column += 1
        }
    }

    fun getToken(): Token {
        val startRow = row
        val startColumn = column
        val buffer = mutableListOf<Char>()

        var code = last ?: stream.read()
        var state = automaton.startState
        while (true) {
            val nextState = automaton.next(state, code)
            if (nextState == ERROR_STATE) break

            state = nextState
            updatePosition(code)
            buffer.add(code.toChar())
            code = stream.read()
        }
        last = code

        if (automaton.finalStates.contains(state)) {
            val symbol = automaton.symbol(state)
            return if (symbol == SKIP_SYMBOL) {
                getToken()
            } else {
                val lexeme = String(buffer.toCharArray())
                Token(symbol, lexeme, startRow, startColumn)
            }
        } else {
            throw Error("Invalid pattern at ${row}:${column}")
        }
    }
}

fun name(symbol: Int) =
    when (symbol) {
        INT -> "int"
        VAR -> "variable"
        PLUS -> "plus"
        MINUS -> "minus"
        LPAREN -> "lparen"
        RPAREN -> "rparen"
        LSPAREN -> "lsparen"
        RSPAREN -> "rsparen"
        LCPAREN -> "lcparen"
        RCPAREN -> "rcparen"
        ASSIGN -> "assign"
        CITY -> "city"
        STREET -> "street"
        POINT -> "point"
        INSTITUTION -> "institution"
        SQUARE -> "square"
        STATUE -> "statue"
        STRING -> "string"
        BLOCK -> "block"
        BEND -> "bend"
        LINE -> "line"
        COMMA -> "comma"
        ADDRESS -> "address"
        DEC_STRING -> "stringVar"
        DEC_COORD -> "coordinateVar"
        DEC_INT -> "intVar"
        EVENTS -> "events"
        FST -> "first"
        SND -> "second"
        else -> throw Error("Invalid symbol")
    }

fun printTokens(scanner: Scanner) {
    val token = scanner.getToken()
    if (token.symbol != EOF_SYMBOL) {
        print("${name(token.symbol)}(\"${token.lexeme}\") ")
        printTokens(scanner)
    }
}

class Coordinate(val name: String, var longtitude: Int, var latitude: Int)

class Parser(private val scanner: Scanner) {
    private var last: Token? = null

    private val variableStringMap = mutableMapOf<String, String>()
    private val variableIntMap = mutableMapOf<String, Int>()
    private var variableCoordPair: MutableList<Coordinate> = mutableListOf()

    private val insideVariableStringMap = mutableMapOf<String, String>()
    private val insideVariableIntMap = mutableMapOf<String, Int>()
    private var insideVariableCoordPair: MutableList<Coordinate> = mutableListOf()

    fun parse(): Boolean {
        last = scanner.getToken()
        val status = City()
        return when (last?.symbol) {
            EOF_SYMBOL -> status
            else -> false
        }
    }

    fun City(): Boolean {
        return recognizeTerminal(CITY) && recognizeTerminal(STRING) && recognizeTerminal(LCPAREN) && Expression() && recognizeTerminal(RCPAREN)
    }

    fun Expression(): Boolean {
        return Operations() && MandatoryElementsList() && AdditionalElementsList()
    }

    fun Operations(): Boolean {
        if (Operation() && Operations())
            return true
        else return true
    }

    fun Operation(): Boolean {
        if(last?.symbol == DEC_STRING) {
            return AssignString()
        } else if(last?.symbol == DEC_INT) {
            return AssignInt()
        } else if(last?.symbol == DEC_COORD) {
            return AssignCoord()
        } else
            return false
    }

    // IntExpr = int IntExpr' | var IntExpr'
    // IntExpr' = + IntExpr | - IntExpr | e

    fun IntExpr(): Pair<Boolean, Int?> {
        if (last?.symbol == INT) {
            val intValue = last?.lexeme?.toInt()
            recognizeTerminal(INT)
            if (intValue != null) {
                return IntExprPrime(intValue)
            }
        } else if (last?.symbol == VAR) {
            val stringValue = last?.lexeme
            recognizeTerminal(VAR)
            var foundValue = insideVariableIntMap[stringValue]
            if (foundValue == null) {
                foundValue = variableIntMap[stringValue]
            }
            if (foundValue != null) {
                return IntExprPrime(foundValue)
            }
        }
        return Pair(false, null)
    }

    fun IntExprPrime(inValue: Int): Pair<Boolean, Int?> {
        if (last?.symbol == PLUS) {
            recognizeTerminal(PLUS)
            val result = IntExpr()
            if (result.first) {
                val computedValue = inValue + result.second!!
                return Pair(true, computedValue)
            }
        } else if (last?.symbol == MINUS) {
            recognizeTerminal(MINUS)
            val result = IntExpr()
            if (result.first) {
                val computedValue = inValue - result.second!!
                return Pair(true, computedValue)
            }
        }
        return Pair(true, inValue)
    }


    fun InsideOperations(): Boolean {
        if (InsideOperation() && InsideOperations())
            return true
        else return true
    }

    fun InsideOperation(): Boolean {
        if(last?.symbol == DEC_STRING) {
            return InsideAssignString()
        } else if(last?.symbol == DEC_INT) {
            return InsideAssignInt()
        } else if(last?.symbol == DEC_COORD) {
            return InsideAssignCoord()
        } else
            return false
    }

    fun MandatoryElementsList(): Boolean {
        return MandatoryElements() && MandatoryElementsPrime()
    }

    fun MandatoryElements(): Boolean {
        if(last?.symbol == STREET) {
            return Streets()
        } else if (last?.symbol == INSTITUTION) {
            return Institutions()
        }  else return false
    }

    fun MandatoryElementsPrime(): Boolean {
        if (MandatoryElementsList())
            return true
        else return true
    }

    fun AdditionalElementsList(): Boolean {
        if(AdditionalElements() && AdditionalElementsList())
            return true
        else return true
    }

    fun AdditionalElements(): Boolean {
        if (last?.symbol == SQUARE) {
            return Squares()
        } else if (last?.symbol == STATUE) {
            return Statues()
        } else return false
    }

    fun Streets(): Boolean {
        return Street() && StreetPrime()
    }

    fun Street(): Boolean {
        if (recognizeTerminal(STREET) && recognizeTerminal(STRING) && recognizeTerminal(LCPAREN) && InsideOperations() && Bend() && Line() && recognizeTerminal(RCPAREN)) {
            insideVariableStringMap.clear()
            insideVariableIntMap.clear()
            insideVariableCoordPair.clear()
            return true
        } else return false
    }

    fun StreetPrime(): Boolean {
        if (Streets())
            return true
        else return true
    }

    fun Institutions(): Boolean {
        return Institution() && InstitutionPrime()
    }

    fun Institution(): Boolean {
        if (recognizeTerminal(INSTITUTION) && recognizeTerminal(STRING) && recognizeTerminal(LCPAREN) && InsideOperations() && Address() && Events() && Block() && recognizeTerminal(RCPAREN)) {
            insideVariableStringMap.clear()
            insideVariableIntMap.clear()
            insideVariableCoordPair.clear()
            return true
        } else return false
    }

    fun InstitutionPrime(): Boolean {
        if (Institutions())
            return true
        else return true
    }

    fun Squares(): Boolean {
        if (Square() && Squares())
            return true
        else return true
    }

    fun Square(): Boolean {
        if (recognizeTerminal(SQUARE) && recognizeTerminal(STRING) && recognizeTerminal(LCPAREN) && InsideOperations() && Block() && recognizeTerminal(RCPAREN)) {
            insideVariableStringMap.clear()
            insideVariableIntMap.clear()
            insideVariableCoordPair.clear()
            return true
        } else return false
    }

    fun Statues(): Boolean {
        if (Statue() && Statues())
            return true
        else return true
    }

    fun Statue(): Boolean {
        if (recognizeTerminal(STATUE) && recognizeTerminal(STRING) && recognizeTerminal(LCPAREN) && InsideOperations() && Point() && recognizeTerminal(RCPAREN)) {
            insideVariableStringMap.clear()
            insideVariableIntMap.clear()
            insideVariableCoordPair.clear()
            return true
        } else return false
    }

    fun Point(): Boolean {
        if (recognizeTerminal(POINT)) {
            return Coordinate()
        }
        return false
    }

    fun Block(): Boolean {
        if (recognizeTerminal(BLOCK)) {
            return recognizeTerminal(LPAREN) && Coordinate() && recognizeTerminal(COMMA) && Coordinate() && recognizeTerminal(COMMA) && Coordinate() && recognizeTerminal(COMMA) && Coordinate() && recognizeTerminal(RPAREN)
        }
        return false
    }

    fun Bend(): Boolean {
        if (recognizeTerminal(BEND)) {
            return recognizeTerminal(LPAREN) && Coordinate() && recognizeTerminal(COMMA) && Coordinate() && recognizeTerminal(COMMA) && Angle() && recognizeTerminal(RPAREN)
        }
        return false
    }

    fun Line(): Boolean {
        if (recognizeTerminal(LINE)) {
            return recognizeTerminal(LPAREN) && Coordinate() && recognizeTerminal(COMMA) && Coordinate() && recognizeTerminal(RPAREN)
        }
        return false
    }

    fun Coordinate(): Boolean {
        if (recognizeTerminal(LPAREN)) {
            if (IntExpr().first) {
                if(recognizeTerminal(COMMA)) {
                    if (IntExpr().first) {
                        return (recognizeTerminal(RPAREN))
                    } else if (First()) {
                        return recognizeTerminal(RPAREN)
                    } else if (Second()) {
                        return recognizeTerminal(RPAREN)
                    } else return false
                } else return false
            } else if (First()) {
                if (recognizeTerminal(COMMA)) {
                    if (IntExpr().first) {
                        return (recognizeTerminal(RPAREN))
                    } else if (First()) {
                        return recognizeTerminal(RPAREN)
                    } else if (Second()) {
                        return recognizeTerminal(RPAREN)
                    } else return false
                } else return false
            } else if (Second()) {
                if (recognizeTerminal(COMMA)) {
                    if (IntExpr().first) {
                        return (recognizeTerminal(RPAREN))
                    } else if (First()) {
                        return recognizeTerminal(RPAREN)
                    } else if (Second()) {
                        return recognizeTerminal(RPAREN)
                    } else return false
                } else return false
            } else return false
        } else if (last?.symbol == VAR) {
            val stringValue = last?.lexeme
            recognizeTerminal(VAR)
            return ((variableCoordPair.find { it.name == stringValue } != null) || (insideVariableCoordPair.find { it.name == stringValue } != null))
        } else return false
    }

    fun AssignString(): Boolean {
        if (recognizeTerminal(DEC_STRING) && last?.symbol == VAR) {
            val variableName = last?.lexeme
            recognizeTerminal(VAR)
            if (recognizeTerminal(ASSIGN) && last?.symbol == STRING) {
                val stringValue = last?.lexeme
                recognizeTerminal(STRING)
                val cleanedStringValue = stringValue?.removeSurrounding("\"")

                if (variableName != null && cleanedStringValue != null) {
                    variableStringMap[variableName] = cleanedStringValue
                    return true
                }
            }
        }
        return false
    }

    fun AssignInt(): Boolean {
        if (recognizeTerminal(DEC_INT) && last?.symbol == VAR) {
            val variableName = last?.lexeme
            recognizeTerminal(VAR)
            if (recognizeTerminal(ASSIGN) && last?.symbol == INT) {
                val intValue =  last?.lexeme?.toIntOrNull()
                IntExpr()
                if (variableName != null && intValue != null) {
                    variableIntMap[variableName] = intValue
                    return true
                }
            }
        }
        return false
    }

    fun AssignCoord(): Boolean {
        if (recognizeTerminal(DEC_COORD) && last?.symbol == VAR) {
            val variableName = last?.lexeme
            recognizeTerminal(VAR)
            if (recognizeTerminal(ASSIGN) && recognizeTerminal(LPAREN) && last?.symbol == INT) {
                val leftIntValue =  last?.lexeme?.toIntOrNull()
                IntExpr()
                if(recognizeTerminal(COMMA)) {
                    val rightIntValue =  last?.lexeme?.toIntOrNull()
                    IntExpr()
                    if (variableName != null && leftIntValue != null && rightIntValue != null) {
                        val newCoord = Coordinate(variableName, leftIntValue, rightIntValue)
                        if (recognizeTerminal(RPAREN)) {
                            variableCoordPair.add(newCoord)
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    fun InsideAssignString(): Boolean {
        if (recognizeTerminal(DEC_STRING) && last?.symbol == VAR) {
            val variableName = last?.lexeme
            recognizeTerminal(VAR)
            if (recognizeTerminal(ASSIGN) && last?.symbol == STRING) {
                val stringValue = last?.lexeme
                recognizeTerminal(STRING)
                val cleanedStringValue = stringValue?.removeSurrounding("\"")

                if (variableName != null && cleanedStringValue != null) {
                    insideVariableStringMap[variableName] = cleanedStringValue
                    return true
                }
            }
        }
        return false
    }

    fun InsideAssignInt(): Boolean {
        if (recognizeTerminal(DEC_INT) && last?.symbol == VAR) {
            val variableName = last?.lexeme
            recognizeTerminal(VAR)
            if (recognizeTerminal(ASSIGN) && last?.symbol == INT) {
                val intValue =  last?.lexeme?.toIntOrNull()
                IntExpr()
                if (variableName != null && intValue != null) {
                    insideVariableIntMap[variableName] = intValue
                    return true
                }
            }
        }
        return false
    }

    fun InsideAssignCoord(): Boolean {
        if (recognizeTerminal(DEC_COORD) && last?.symbol == VAR) {
            val variableName = last?.lexeme
            recognizeTerminal(VAR)
            if (recognizeTerminal(ASSIGN) && recognizeTerminal(LPAREN) && last?.symbol == INT) {
                val leftIntValue =  last?.lexeme?.toIntOrNull()
                IntExpr()
                if(recognizeTerminal(COMMA)) {
                    val rightIntValue =  last?.lexeme?.toIntOrNull()
                    IntExpr()
                    if (variableName != null && leftIntValue != null && rightIntValue != null) {
                        val newCoord = Coordinate(variableName, leftIntValue, rightIntValue)
                        if (recognizeTerminal(RPAREN)) {
                            insideVariableCoordPair.add(newCoord)
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    private fun Angle(): Boolean {
        if (IntExpr().first) {
            return true
        } else if (last?.symbol == VAR) {
            val stringValue = last?.lexeme
            recognizeTerminal(VAR)
            return variableIntMap.keys.find { it == stringValue } != null
        } else return false
    }

    private fun Address(): Boolean {
        if (recognizeTerminal(ADDRESS) && recognizeTerminal(ASSIGN)) {
            if (recognizeTerminal(STRING)) {
                return true
            } else if (last?.symbol == VAR) {
                val stringValue = last?.lexeme
                recognizeTerminal(VAR)
                var foundValue = insideVariableStringMap.keys.find { it == stringValue }
                if (foundValue == null) {
                    foundValue = variableStringMap.keys.find { it == stringValue }
                }
                return foundValue != null
            } else return false
        } else return false
    }

    private fun Events(): Boolean {
        if (recognizeTerminal(EVENTS) && recognizeTerminal(ASSIGN)) {
            if (IntExpr().first) {
                return true
            } else if (last?.symbol == VAR) {
                val stringValue = last?.lexeme
                recognizeTerminal(VAR)
                var foundValue = insideVariableIntMap.keys.find { it == stringValue }
                if (foundValue == null) {
                    foundValue = variableIntMap.keys.find { it == stringValue }
                }
                return foundValue != null
            } else return false
        } else return false
    }

    private fun First(): Boolean {
        if(recognizeTerminal(FST) && recognizeTerminal(LPAREN)) {
            if (last?.symbol == VAR) {
                val stringValue = last?.lexeme
                recognizeTerminal(VAR)
                var foundValue = insideVariableCoordPair.find { it.name == stringValue }
                if (foundValue == null) {
                    foundValue = variableCoordPair.find { it.name == stringValue }
                    recognizeTerminal(RPAREN)
                }
                return foundValue != null
            }
            return false
        }
        return false
    }

    private fun Second(): Boolean {
        if(recognizeTerminal(SND) && recognizeTerminal(LPAREN)) {
            if (last?.symbol == VAR) {
                val stringValue = last?.lexeme
                recognizeTerminal(VAR)
                var foundValue = insideVariableCoordPair.find { it.name == stringValue }
                if (foundValue == null) {
                    foundValue = variableCoordPair.find { it.name == stringValue }
                    recognizeTerminal(RPAREN)
                }
                return foundValue != null
            }
            return false
        }
        return false
    }

    private fun recognizeTerminal(value: Int) =
        if (last?.symbol == value) {
            last = scanner.getToken()
            true
        } else false

}

fun main(args: Array<String>) {
    if (Parser(Scanner(ForForeachFFFAutomaton, File("C:\\Users\\marij\\Desktop\\Praktikum\\Git_Prevajanje\\PrevajanjePJ\\src\\test.txt").inputStream())).parse()) {
        println("accept")
    } else {
        println("reject")
    }

    /* val inputString = "string A = \"This is a string\"" // Update the input string here
    val inputStream: InputStream = inputString.byteInputStream()
    val scanner = Scanner(ForForeachFFFAutomaton, inputStream)
    printTokens(scanner)  */
}