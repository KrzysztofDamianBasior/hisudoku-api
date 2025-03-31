package org.hisudoku.hisudokuapi.sudokus.services;

import org.hisudoku.hisudokuapi.sudokus.entities.Sudoku;
import org.hisudoku.hisudokuapi.sudokus.models.SudokuModel;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class SudokuUtils {
    public static List<SudokuModel> mapToSudokuModelFavouritedByNullAuthorNullDTOs(List<Sudoku> sudoku) {
        return sudoku.stream()
                .map(SudokuUtils::mapToSudokuModelFavouritedByNullAuthorNullDTO)
                .collect(Collectors.toList());
    }

    public static SudokuModel mapToSudokuModelFavouritedByNullAuthorNullDTO(Sudoku sudoku) {
        return new SudokuModel(
                sudoku.getId(),
                sudoku.getCreatedAt(),
                sudoku.getUpdatedAt(),
                null,
                sudoku.getContent(),
                sudoku.getFavouriteCount(),
                null
        );
    }

    public static List<Integer> extractColumnFromTheBoard(List<List<Integer>> board, int columnNum) {
//      T reduce(T identity, BinaryOperator<T> accumulator)
//      Identity – an element that is the initial value of the reduction operation and the default result if the stream is empty
//      Accumulator – a function that takes two parameters: a partial result of the reduction operation and the next element of the stream
//      Combiner – a function used to combine the partial result of the reduction operation when the reduction is parallelized or when there’s a mismatch between the types of the accumulator arguments and the types of the accumulator implementation
//      If we use sequential streams and the types of the accumulator arguments and the types of its implementation match, we don’t need to use a combiner.

        ArrayList<Integer> columnFromTheBoard = new ArrayList<>(10);
        return board.stream().reduce(columnFromTheBoard, (subtotal, row) -> {
            subtotal.add(row.get(columnNum)); // will always return true
            return subtotal;
        });
    }

    public static boolean validateIfFragmentIsSolved(List<Integer> sudokuValues) {
//        Collections.sort(List<T>) dumps the specified list into an array, sorts the array, and iterates over the list resetting each element from the corresponding position in the array. Collections.sort() was made to work with any List implementation, and that's why it's not working in place (merging LinkedList in place is difficult and sacrifices stability).
//        List interface has default void sort(Comparator<? super E> c) API, using which you can sort an instance in place. The list must be modifiable, iterable and its elements comparable to each other. The in-place algorithms are those that don’t need any auxiliary data structure in order to transform the input data. Basically, it means that the algorithm doesn’t use extra space for input manipulation. It practically overrides the input with the output.
//        Collections.sort(collection, Collections.reverseOrder());
//        Arrays.sort(numbers);

        // The compare(x, y) returns -1 if x is less than y, 0 if they’re equal, and 1 otherwise.
        String fragment = sudokuValues.stream().sorted((x, y) -> x - y).map(Object::toString).collect(Collectors.joining(""));
        String passingFragment = "123456789";
        return fragment.equals(passingFragment);
    }

    public static boolean validateIfBoardIsValid(List<List<Integer>> board) {
        // Arrays.asList returns a mutable list while the list returned by List.of is structurally immutable
        // Arrays.asList allows null elements while List.of doesn't
        List<List<Integer>> rows = Arrays.asList(
                new ArrayList<>(10),
                new ArrayList<>(10),
                new ArrayList<>(10),
                new ArrayList<>(10),
                new ArrayList<>(10),
                new ArrayList<>(10),
                new ArrayList<>(10),
                new ArrayList<>(10),
                new ArrayList<>(10),
                new ArrayList<>(10)
        );
        List<List<Integer>> columns = Arrays.asList(
                new ArrayList<>(10),
                new ArrayList<>(10),
                new ArrayList<>(10),
                new ArrayList<>(10),
                new ArrayList<>(10),
                new ArrayList<>(10),
                new ArrayList<>(10),
                new ArrayList<>(10),
                new ArrayList<>(10),
                new ArrayList<>(10)
        );
        List<List<Integer>> boxes = Arrays.asList(
                new ArrayList<>(10),
                new ArrayList<>(10),
                new ArrayList<>(10),
                new ArrayList<>(10),
                new ArrayList<>(10),
                new ArrayList<>(10),
                new ArrayList<>(10),
                new ArrayList<>(10),
                new ArrayList<>(10),
                new ArrayList<>(10)
        );

        // AtomicBoolean are used for flags, locks, or signaling
        // The main difference between Boolean and AtomicBoolean is that Boolean is immutable and cannot be changed once created, whereas AtomicBoolean is mutable and can be modified using atomic operations.
        // AtomicBoolean breakInnerLoopWithFalse = new AtomicBoolean(false);

        int[] rowIndexes = IntStream.range(0, board.size()).toArray();

        for (int rowIndex : rowIndexes) {
            List<Integer> boardRow = board.get(rowIndex);

            int[] colIndexes = IntStream.range(0, boardRow.size()).toArray();

            for (int colIndex : colIndexes) {
                Integer num = boardRow.get(colIndex);

                if (num != 0) {
                    if (rows.get(rowIndex).contains(num)) {
                        return false;
                    } else rows.get(rowIndex).add(num);

                    if (columns.get(colIndex).contains(num)) {
                        return false;
                    } else columns.get(colIndex).add(num);

                    // Math.floor() returns a double that is "equal to a mathematical integer". If the argument is NaN or an infinity or positive zero or negative zero, then the result is the same as the argument. Can't do that with an int. The largest double value is also larger than the largest int. The double data-type has a 53 bit mantissa. Among other things that means that a double can represent all whole up to 2^53 without precision loss. If you store such a large number in an integer you will get an overflow. Integers only have 32 bits.
                    int boxIndex = (int) Math.floor(rowIndex / 3.0) * 3 + (int) Math.floor(colIndex / 3.0);

                    if (boxes.get(boxIndex).contains(num)) {
                        return false;
                    } else boxes.get(boxIndex).add(num);
                }
            }
        }
        return true;
    }

    public static List<List<Integer>> stringToBoard(String sudokuString) throws Exception {
        // The split() method treats the delimiter as a regular expression, so we can use regex patterns for splitting. However, this also means that certain characters, like dots (.), backslashes (\), and other metacharacters, need to be escaped.
        // s.split("\\.")
        // s.split("[,;|]")         split by , ; and |
        // s.split("[\\D]")
        // s.split(" ")
        // s.split("[,\\.\\s]")     split a string by spaces, and also punctuation
        String[] rows = sudokuString.split(";");

        List<List<Integer>> board = new ArrayList<>(10);

        for (String row : rows) {
            // alphabets followed by digits: "[A-Za-z]+\\d+" // The double backslash is used to escape the backslash character because it’s a special character in Java.
            // case-insensitive alphabets: "[a-zA-Z]+"
            // email address: "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
            // one or more digits: "\\d+"
            // "cat|dog|fish"

            boolean matches = row.matches("\\d+");
            if (!matches) {
                throw new Exception("invalid sudoku board");
            }
            // Creating Stream of Regex Matches for data extraction
            // We aim to extract all the numbers from a string using a regular expression and then create a stream of these matches.
            String regex = "[0-9]"; // "[0-9]" extracts every single digit from presented string, "\\d+" extracts every number as a whole
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(row);

            // The matcher.results() method returns a stream of MatchResult objects. Each object corresponds to a distinct match found in the input string based on the regex pattern. Then, we can use the group() method of this object to get the matched String.
            List<String> extractedNumbersAsStrings = matcher.results().map(MatchResult::group).toList();
            // "1234,5;2tr67" will produce [1,2,3,4,5,6,7]

            List<Integer> extractedNumbersAsIntegers = extractedNumbersAsStrings.stream().map(Integer::parseInt).toList();
            board.add(extractedNumbersAsIntegers);
        }
        return board;
    }

    public static boolean validateIfStringRepresentsValidSudokuString(String string) {
        //String string = "111.111.111;222.222.222;333.333.333;444.444.444;555.555.555;666.666.666;777.777.777;888.888.888;999.999.999;";
        return string.matches("^([0-9]{3}\\.[0-9]{3}\\.[0-9]{3};){9}$");
    }

    public static boolean validateIfFragmentIsValid(List<String> array, String emptyCharacter) {
        List<String> digits = array.stream().filter(character -> !character.equals(emptyCharacter)).toList();
        return digits.stream().distinct().toList().size() == digits.size();
    }

    public static boolean validateIfStringRepresentsValidSudokuBoard(String sudokuString) throws Exception {
        /*
            Check each row for duplicates.
            Check each column for duplicates.
            Check each 3x3 sub grid for duplicates.
            Return false if any duplicates are found, and true if no duplicates are found.
        */
        //reorganize data
        List<List<Integer>> board = stringToBoard(sudokuString);
        return validateIfBoardIsValid(board);
    }
}
