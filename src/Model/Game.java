package Model;

public class Game {

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

    public Game() {
        gameString = "012345678";
        lastMarker = '.'; // Default value
    }

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

    public boolean checkForWin() {
        if ((gameString.charAt(0) == gameString.charAt(1) && gameString.charAt(1) == gameString.charAt(2)) || // Top row
            (gameString.charAt(3) == gameString.charAt(4) && gameString.charAt(4) == gameString.charAt(5)) || // Middle row
            (gameString.charAt(6) == gameString.charAt(7) && gameString.charAt(7) == gameString.charAt(8)) || // Bottom row
            (gameString.charAt(0) == gameString.charAt(3) && gameString.charAt(3) == gameString.charAt(6)) || // Left Column
            (gameString.charAt(1) == gameString.charAt(4) && gameString.charAt(4) == gameString.charAt(7)) || // Middle Column
            (gameString.charAt(2) == gameString.charAt(5) && gameString.charAt(5) == gameString.charAt(8)) || // Right Column
            (gameString.charAt(0) == gameString.charAt(4) && gameString.charAt(4) == gameString.charAt(8)) || // Right to Left Diagonal
            (gameString.charAt(2) == gameString.charAt(4) && gameString.charAt(4) == gameString.charAt(6))) { // Left to Right Diagonal
            return true;
        }
        return false;
    }

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

    private void applyMove() {
        gameString = gameString.replace(Character.forDigit(currentIndex, 10), currentMarker);
        lastMarker = currentMarker;
    }

    public String getGameString() {
        return gameString;
    }
}
