package Model;

/**
 * Models the game as a string and provides convience methods for editing
 * the string and decoding the moves that come across the network
 */
public class Game {

    /**
     * All of the response codes that can occur when
     * decoding a move string
     */
    public enum DecodeMoveReturnCode {
        OK                                  ("Decoding succeeded"),
        WRONG_FORMATTING                    ("Move string formatted incorrectly"),
        UNPARSABLE_INDEX                    ("Index was not an integer"),
        INDEX_OUT_OF_RANGE                  ("Index is out of range of game string"),
        INDEX_TAKEN                         ("Index is already taken"),
        INVALID_MARKER_LENGTH               ("Marker length is invalid"),
        INVAILD_MARKER_TOKEN                ("Marker is invalid. Should be 'X' or 'O'"),
        TWO_SEQUENTIAL_MOVES_WITH_SAME_TOKEN("Two moves were decoded with the same token");

        private String message;
        private DecodeMoveReturnCode(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    private String gameString;
    private char lastMarker;

    private int currentIndex;
    private char currentMarker;

    /**
     * Creates a new, blank game
     */
    public Game() {
        gameString = "012345678";
        lastMarker = '.'; // Default value
    }

    /**
     * Attempts to make a move
     * @param moveString        encoded move string
     * @return                  true if the move succeeded in applying
     */
    public boolean makeMove(String moveString) {
        DecodeMoveReturnCode returnCode = decodeMove(moveString);
        if (returnCode != DecodeMoveReturnCode.OK) {
            System.err.println("Error decoding move: " + returnCode.getMessage());
            System.err.println("Cancelling from making a move");
            return false;
        }

        applyMove();
        return true;
    }

    /**
     * Checks to see if there is a winner on the board
     * @return                  true if there is a winner
     */
    public boolean checkForWin() {
        return ((gameString.charAt(0) == gameString.charAt(1) && gameString.charAt(1) == gameString.charAt(2)) || // Top row
                (gameString.charAt(3) == gameString.charAt(4) && gameString.charAt(4) == gameString.charAt(5)) || // Middle row
                (gameString.charAt(6) == gameString.charAt(7) && gameString.charAt(7) == gameString.charAt(8)) || // Bottom row
                (gameString.charAt(0) == gameString.charAt(3) && gameString.charAt(3) == gameString.charAt(6)) || // Left Column
                (gameString.charAt(1) == gameString.charAt(4) && gameString.charAt(4) == gameString.charAt(7)) || // Middle Column
                (gameString.charAt(2) == gameString.charAt(5) && gameString.charAt(5) == gameString.charAt(8)) || // Right Column
                (gameString.charAt(0) == gameString.charAt(4) && gameString.charAt(4) == gameString.charAt(8)) || // Right to Left Diagonal
                (gameString.charAt(2) == gameString.charAt(4) && gameString.charAt(4) == gameString.charAt(6))); // Left to Right Diagonal

    }

    /**
     * Checks to see if there is a tie on the board
     * @return                  true if the board is a tie
     */
    public boolean checkForTie() {
        if (checkForWin()) {
            return false;
        }
        for (char c : gameString.toCharArray()) {
            if (c != 'X' && c != 'O') {
                return false;
            }
        }
        return true;
    }

    /**
     * Decodes a move string from the network, and creates a
     * safe index and marker to use to apply the move
     * @param moveString            move string from a player
     * @return                      return code of the decoding status
     */
    private DecodeMoveReturnCode decodeMove(String moveString) {
        String[] maybeMoveDecoded = moveString.split(":");
        int index;
        char marker;

        // Ensure the length is correct
        if(maybeMoveDecoded.length != 2) {
            return DecodeMoveReturnCode.WRONG_FORMATTING;
        }

        // Ensure that we can decode the index
        try {
            index = Integer.parseInt(maybeMoveDecoded[0]);
        }
        catch(NumberFormatException e) {
            return DecodeMoveReturnCode.UNPARSABLE_INDEX;
        }

        // Ensure the index is in range
        if (index < 0 || index > gameString.length()) {
            return DecodeMoveReturnCode.INDEX_OUT_OF_RANGE;
        }

        // Ensure the index is not already taken
        if (gameString.charAt(index) != String.valueOf(index).charAt(0)) {
            return DecodeMoveReturnCode.INDEX_TAKEN;
        }
        // Index is good now
        // Ensure the marker is the correct length
        if (maybeMoveDecoded[1].length() != 1) {
            return DecodeMoveReturnCode.INVALID_MARKER_LENGTH;
        }

        marker = maybeMoveDecoded[1].charAt(0);

        // Ensure that the marker is an X or an O
        if (marker != 'X' && marker != 'O') {
            return DecodeMoveReturnCode.INVAILD_MARKER_TOKEN;
        }

        // Ensure that it is the proper marker's turn
        if (lastMarker == marker) {
            return DecodeMoveReturnCode.TWO_SEQUENTIAL_MOVES_WITH_SAME_TOKEN;
        }

        currentIndex = index;
        currentMarker = marker;

        return DecodeMoveReturnCode.OK;
    }

    /**
     * Actually applies the move to the board
     * Assumes that there is a correct move here already
     */
    private void applyMove() {
        gameString = gameString.replace(Character.forDigit(currentIndex, 10), currentMarker);
        lastMarker = currentMarker;
    }

    /**
     * Gets the game string
     * @return      Immutable version of the game string
     */
    public String getGameString() {
        return gameString;
    }
}
