package com.company;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

//TO IMPLEMENT:
// - DEBUG
// - ATTACKER SURROUND VICTORY CONDITION
// --DEPTH-FIRST SEARCH OR OTHER PATHFINDING ALGORITHM
// - DEFENDER EDGE FORT VICTORY CONDITION
// - BOARD STATE REPETITION ILLEGAL
// - MAIN MENU WITH NEW GAME, SAVE GAME, LOAD GAME, RULES
// - USER PROFILES WITH WIN/LOSS ATTACKER/DEFENDER STATS
// - ARTIFICIAL INTELLIGENCE

public class Main {
    private static int[][] hidden_board = new int[11][11]; //WILL BE USED FOR DEPTH-FIRST SEARCH TO CALCULATE IF DEFENDER CAN REACH EDGE

    private static void generate_board(String[][] template) {
        for (int j=0; j<11; j++) {
            for (int i=0; i<11; i++) {
                set_board_state(i, j, template[i][j]);
            }
        }
    }

    private static void generate_hidden_board() {
        for (int j=0; j<11; j++) {
            for (int i=0; i<11; i++) {
                switch(get_board_state(i,j)) {
                    case "{ }":
                        hidden_board[i][j] = 0;
                    case "[ ]":
                        hidden_board[i][j] = 0;
                    case "[D]":
                        hidden_board[i][j] = 1;
                    case "[A]":
                        hidden_board[i][j] = 2;
                    case "[K]":
                        hidden_board[i][j] = 1;
                    case "{K}":
                        hidden_board[i][j] = 1;
                }
            }
        }
    }

    private static void print_board() {
        System.out.println("    A  B  C  D  E  F  G  H  I  J  K ");
        for (int y = 0; y < 11; y++) {
            int row = y + 1;
            if (row<10) {
                System.out.print(row + "  ");
            }
            else {
                System.out.print(row + " ");
            }
            for (int x = 0; x < 11; x++) {
                System.out.print(Board[x][y]);
            }
            if (y<9) {
                System.out.print("  " + row);
            }
            else {
                System.out.print(" " + row);
            }
            System.out.print("\n");
        }
        System.out.println("    A  B  C  D  E  F  G  H  I  J  K ");
    }

    private static boolean attacker_turn = false;

    private static String DEFAULT_NORMAL = "[ ]";
    private static String DEFAULT_HOSTILE = "{ }";
    private static String DEFENDER_PIECE = "[D]";
    private static String ATTACKER_PIECE = "[A]";
    private static String KING_PIECE_NORMAL = "[K]";
    private static String KING_PIECE_HOSTILE = "{K}";

    private static String[][] Board = new String[11][11];

    static String[][] normal_setup = new String[][]{{DEFAULT_HOSTILE, DEFAULT_NORMAL, DEFAULT_NORMAL, ATTACKER_PIECE, ATTACKER_PIECE, ATTACKER_PIECE, ATTACKER_PIECE, ATTACKER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_HOSTILE},
    {DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, ATTACKER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL},
    {DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL},
    {ATTACKER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFENDER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, ATTACKER_PIECE},
    {ATTACKER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFENDER_PIECE, DEFENDER_PIECE, DEFENDER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, ATTACKER_PIECE},
    {ATTACKER_PIECE, ATTACKER_PIECE, DEFAULT_NORMAL, DEFENDER_PIECE, DEFENDER_PIECE, KING_PIECE_HOSTILE, DEFENDER_PIECE, DEFENDER_PIECE, DEFAULT_NORMAL, ATTACKER_PIECE, ATTACKER_PIECE},
    {ATTACKER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFENDER_PIECE, DEFENDER_PIECE, DEFENDER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, ATTACKER_PIECE},
    {ATTACKER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFENDER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, ATTACKER_PIECE},
    {DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL},
    {DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, ATTACKER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL},
    {DEFAULT_HOSTILE, DEFAULT_NORMAL, DEFAULT_NORMAL, ATTACKER_PIECE, ATTACKER_PIECE, ATTACKER_PIECE, ATTACKER_PIECE, ATTACKER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_HOSTILE}};

    //FOR TESTING
    static String[][] Defender_Win_King = new String[][]{{DEFAULT_HOSTILE, KING_PIECE_NORMAL, DEFAULT_NORMAL, ATTACKER_PIECE, ATTACKER_PIECE, ATTACKER_PIECE, ATTACKER_PIECE, ATTACKER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_HOSTILE},
            {DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, ATTACKER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL},
            {DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL},
            {ATTACKER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFENDER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, ATTACKER_PIECE},
            {ATTACKER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFENDER_PIECE, DEFENDER_PIECE, DEFENDER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, ATTACKER_PIECE},
            {ATTACKER_PIECE, ATTACKER_PIECE, DEFAULT_NORMAL, DEFENDER_PIECE, DEFENDER_PIECE, DEFAULT_HOSTILE, DEFENDER_PIECE, DEFENDER_PIECE, DEFAULT_NORMAL, ATTACKER_PIECE, ATTACKER_PIECE},
            {ATTACKER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFENDER_PIECE, DEFENDER_PIECE, DEFENDER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, ATTACKER_PIECE},
            {ATTACKER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFENDER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, ATTACKER_PIECE},
            {DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL},
            {DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, ATTACKER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL},
            {DEFAULT_HOSTILE, DEFAULT_NORMAL, DEFAULT_NORMAL, ATTACKER_PIECE, ATTACKER_PIECE, ATTACKER_PIECE, ATTACKER_PIECE, ATTACKER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_HOSTILE}};

    //FOR TESTING
    private static String[][] Attacker_Win_King = new String[][]{{DEFAULT_HOSTILE, ATTACKER_PIECE, DEFAULT_NORMAL, ATTACKER_PIECE, ATTACKER_PIECE, ATTACKER_PIECE, ATTACKER_PIECE, ATTACKER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_HOSTILE},
            {ATTACKER_PIECE, KING_PIECE_NORMAL, ATTACKER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, ATTACKER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL},
            {DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL},
            {ATTACKER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFENDER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, ATTACKER_PIECE},
            {ATTACKER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFENDER_PIECE, DEFENDER_PIECE, DEFENDER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, ATTACKER_PIECE},
            {ATTACKER_PIECE, ATTACKER_PIECE, DEFAULT_NORMAL, DEFENDER_PIECE, DEFENDER_PIECE, DEFAULT_HOSTILE, DEFENDER_PIECE, DEFENDER_PIECE, DEFAULT_NORMAL, ATTACKER_PIECE, ATTACKER_PIECE},
            {ATTACKER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFENDER_PIECE, DEFENDER_PIECE, DEFENDER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, ATTACKER_PIECE},
            {ATTACKER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFENDER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, ATTACKER_PIECE},
            {DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL},
            {DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, ATTACKER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_NORMAL},
            {DEFAULT_HOSTILE, DEFAULT_NORMAL, DEFAULT_NORMAL, ATTACKER_PIECE, ATTACKER_PIECE, ATTACKER_PIECE, ATTACKER_PIECE, ATTACKER_PIECE, DEFAULT_NORMAL, DEFAULT_NORMAL, DEFAULT_HOSTILE}};

    private static String get_board_state(int x, int y) { //RETURNS BOARD STATE AT X,Y
        if (x > 10 || x < 0 || y > 10 || y < 0) return "-1"; //CHECK IF COORDINATES OUT OF BOUNDS
        else return Board[x][y];
    }

    private static void set_board_state(int x, int y, String state) {
        Board[x][y] = state;
    }

    private static ArrayList<String> get_valid_move(int x, int y) { //RETURNS STRING OF VALID DESTINATIONS
        ArrayList<String> valid_moves = new ArrayList<>();
        boolean is_king = false;
        //CHECK IF PLAYER MOVING THE KING
        if (get_board_state(x, y).equals("[K]") || get_board_state(x, y).equals("{K}")) {
            is_king = true;
        }
        //CHECK MOVES TO THE LEFT
        for (int i = x-1; i > -1; i--) {
            if (get_board_state(i, y).equals(DEFAULT_NORMAL) || (is_king && get_board_state(i, y).equals(DEFAULT_HOSTILE))) {
                valid_moves.add(Integer.toString(i) + Integer.toString(y));
            } else {
                if (!get_board_state(i, y).equals(DEFAULT_HOSTILE)) {
                    break;
                }
            }
        }
        //CHECK MOVES TO THE RIGHT
        for (int i = x+1; i < 11; i++) {
            if (get_board_state(i, y).equals(DEFAULT_NORMAL) || (is_king && get_board_state(i, y).equals(DEFAULT_HOSTILE))) {
                valid_moves.add(Integer.toString(i) + Integer.toString(y));
            } else {
                if (!get_board_state(i, y).equals(DEFAULT_HOSTILE)) {
                    break;
                }
            }
        }
        //CHECK MOVES DOWN
        for (int j = y-1; j > -1; j--) {
            if (get_board_state(x, j).equals(DEFAULT_NORMAL) || (is_king && get_board_state(x, j).equals(DEFAULT_HOSTILE))) {
                valid_moves.add(Integer.toString(x) + Integer.toString(j));
            } else {
                if (!get_board_state(x, j).equals(DEFAULT_HOSTILE)) {
                    break;
                }
            }
        }
        //CHECK MOVES UP
        for (int j = y+1; j < 11; j++) {
            if (get_board_state(x, j).equals(DEFAULT_NORMAL) || (is_king && get_board_state(x, j).equals(DEFAULT_HOSTILE))) {
                valid_moves.add(Integer.toString(x) + Integer.toString(j));
            } else {
                if (!get_board_state(x, j).equals(DEFAULT_HOSTILE)) {
                    break;
                }
            }
        }
        return valid_moves;
    }

    private static boolean valid_selection(int x, int y){
        if (attacker_turn) {
            return get_board_state(x, y).equals(ATTACKER_PIECE);
        }
        else {
            return get_board_state(x, y).equals(DEFENDER_PIECE) || get_board_state(x, y).equals(KING_PIECE_NORMAL) || get_board_state(x, y).equals(KING_PIECE_HOSTILE);
        }
    }

    private static void move(int x0, int y0, int x1, int y1) {
        if (!attacker_turn) {
            //KING MOVING FROM HOSTILE TO NORMAL
            if (get_board_state(x0, y0).equals(KING_PIECE_HOSTILE)) {
                set_board_state(x1, y1, KING_PIECE_NORMAL);
                set_board_state(x0, y0, DEFAULT_HOSTILE);
                return;
            }
            //KING MOVING FROM NORMAL
            if (get_board_state(x0, y0).equals(KING_PIECE_NORMAL)) {
                //TO HOSTILE
                if (get_board_state(x1, y1).equals(DEFAULT_HOSTILE)) {
                    set_board_state(x1, y1, KING_PIECE_HOSTILE);
                    set_board_state(x0, y0, DEFAULT_NORMAL);
                    return;
                }
                //TO NORMAL
                else {
                    set_board_state(x1, y1, KING_PIECE_NORMAL);
                    set_board_state(x0, y0, DEFAULT_NORMAL);
                    return;
                }
            }
            //DEFENDER MOVING
            else {
                set_board_state(x1, y1, DEFENDER_PIECE);
                set_board_state(x0, y0, DEFAULT_NORMAL);
            }
            //CHECK TO SEE IF KING HAS ESCAPED AND DEFENDER HAS WON

        } else {
            set_board_state(x1, y1, ATTACKER_PIECE);
            set_board_state(x0, y0, DEFAULT_NORMAL);
        }
    }

    private static boolean piece_sandwiched(int x, int y, char pos) { //RETURNS TRUE IF SANDWICHED BETWEEN ENEMY PIECES AND/OR HOSTILE SQUARE
        String[] friendly_pieces;
        //DETERMINE WHICH PIECES TO CHECK FOR
        if (attacker_turn) {
            friendly_pieces = new String[]{ATTACKER_PIECE, DEFAULT_HOSTILE};
        } else {
            friendly_pieces = new String[]{DEFENDER_PIECE, DEFAULT_HOSTILE, KING_PIECE_HOSTILE, KING_PIECE_NORMAL};
        }
        //CHECK FOR HORIZONTAL OR VERTICAL SANDWICHING
        switch (pos) {
            case 'L':
                if (Arrays.asList(friendly_pieces).contains(get_board_state(x - 1, y))) {
                    return true;
                }
            case 'R':
                if (Arrays.asList(friendly_pieces).contains(get_board_state(x + 1, y))) {
                    return true;
                }
            case 'U':
                if (Arrays.asList(friendly_pieces).contains(get_board_state(x, y - 1))) {
                    return true;
                }
            case 'D':
                if (Arrays.asList(friendly_pieces).contains(get_board_state(x, y + 1))) {
                    return true;
                }
        }
        return false;
    }

    private static void captures(int x, int y) {
        if (attacker_turn)
            attacker_captures(x, y);
        else
            defender_captures(x, y);
    }

    private static void attacker_captures(int x, int y) {
        //CAPTURE DEFENDER PIECE
        String capture_message = "You captured a Defender piece!";
        //PIECE TO LEFT
        if (get_board_state(x - 1, y).equals(DEFENDER_PIECE)) {
            if (piece_sandwiched(x - 1, y, 'L')) {
                System.out.println(capture_message);
            }
        }
        //PIECE TO RIGHT
        if (get_board_state(x + 1, y).equals(DEFENDER_PIECE)) {
            if (piece_sandwiched(x + 1, y, 'R')) {
                System.out.println(capture_message);
            }
        }
        //PIECE UP
        if (get_board_state(x, y - 1).equals(DEFENDER_PIECE)) {
            if (piece_sandwiched(x, y - 1, 'U')) {
                System.out.println(capture_message);
            }
        }
        //PIECE DOWN
        if (get_board_state(x, y + 1).equals(DEFENDER_PIECE)) {
            if (piece_sandwiched(x, y + 1, 'D')) {
                System.out.println(capture_message);
            }
        }

        //CAPTURE KING
        capture_message = "You captured the King!";
        //KING TO LEFT
        if (get_board_state(x - 1, y).equals(KING_PIECE_HOSTILE) || get_board_state(x - 1, y).equals(KING_PIECE_NORMAL)) {
            if (king_surrounded(x - 1, y)) {
                System.out.println(capture_message);
                declare_victory(0);
            }
        }
        //KING TO RIGHT
        if (get_board_state(x + 1, y).equals(KING_PIECE_HOSTILE) || get_board_state(x + 1, y).equals(KING_PIECE_NORMAL)) {
            if (king_surrounded(x + 1, y)) {
                System.out.println(capture_message);
                declare_victory(0);
            }
        }
        //KING UP
        if (get_board_state(x, y - 1).equals(KING_PIECE_HOSTILE) || get_board_state(x, y - 1).equals(KING_PIECE_NORMAL)) {
            if (king_surrounded(x, y - 1)) {
                System.out.println(capture_message);
                declare_victory(0);
            }
        }
        //KING DOWN
        if (get_board_state(x, y + 1).equals(KING_PIECE_HOSTILE) || get_board_state(x, y + 1).equals(KING_PIECE_NORMAL)) {
            if (king_surrounded(x, y + 1)) {
                System.out.println(capture_message);
                declare_victory(0);
            }
        }
    }

    private static boolean king_surrounded(int x, int y) { //RETURNS TRUE IF THE KING IS SURROUNDED BY ATTACKERS (AND A HOSTILE SQUARE); RETURNS FALSE OTHERWISE
        if ((get_board_state(x + 1, y).equals(ATTACKER_PIECE) || get_board_state(x + 1, y).equals(DEFAULT_HOSTILE)) &&
                (get_board_state(x - 1, y).equals(ATTACKER_PIECE) || get_board_state(x - 1, y).equals(DEFAULT_HOSTILE)) &&
                (get_board_state(x, y + 1).equals(ATTACKER_PIECE) || get_board_state(x, y + 1).equals(DEFAULT_HOSTILE)) &&
                (get_board_state(x, y - 1).equals(ATTACKER_PIECE) || get_board_state(x, y - 1).equals(DEFAULT_HOSTILE))) {
            return true;
        }
        return false;
    }

    private static void defender_captures(int x, int y) {
        //CAPTURE DEFENDER PIECE
        String capture_message = "You captured an Attacker piece!";
        //PIECE TO LEFT
        if (get_board_state(x - 1, y).equals(ATTACKER_PIECE)) {
            if (piece_sandwiched(x - 1, y, 'L')) {
                System.out.println(capture_message);
            }
        }
        //PIECE TO RIGHT
        if (get_board_state(x + 1, y).equals(ATTACKER_PIECE)) {
            if (piece_sandwiched(x + 1, y, 'R')) {
                System.out.println(capture_message);
            }
        }
        //PIECE UP
        if (get_board_state(x, y - 1).equals(ATTACKER_PIECE)) {
            if (piece_sandwiched(x, y - 1, 'U')) {
                System.out.println(capture_message);
            }
        }
        //PIECE DOWN
        if (get_board_state(x, y + 1).equals(ATTACKER_PIECE)) {
            if (piece_sandwiched(x, y + 1, 'D')) {
                System.out.println(capture_message);
            }
        }
    }

    private static void check_victory() {
        if (get_board_state(0, 0).equals(KING_PIECE_HOSTILE) ||
                get_board_state(0, 10).equals(KING_PIECE_HOSTILE) ||
                get_board_state(10, 0).equals(KING_PIECE_HOSTILE) ||
                get_board_state(10, 10).equals(KING_PIECE_HOSTILE)) {
            declare_victory(1);
        }
    }

    private static void declare_victory(int victor) //0 = ATTACKER, 1 = DEFENDER
    {
        print_board();
        if (victor == 0) {
            System.out.println("The Attacker is victorious!");
        } else {
            System.out.println("The Defender is victorious!");
        }
        System.exit(0);
    }

    public static void main(String[] args) {
        generate_board(normal_setup);
        generate_hidden_board();
        String welcome_message = "Welcome to Hnefatafl! The goal of the game is to capture the king by surrounding him " +
                                 "\non all 4 sides if you are the attacker, or to move your king to a corner and escape " +
                                 "\nif you are the defender. Pieces move in straight lines, like rooks in chess; they " +
                                 "\ncannot move past other pieces, and only the king can move onto the hostile squares " +
                                 "\n{ }. Capture enemy pieces by surrounding them on two opposite sides. Be careful; both" +
                                 "\n sides can use hostile squares to capture pieces!\n\n";
        String start_turn_message;
        Scanner input = new Scanner(System.in);
        String user_x0;
        String user_y0;
        String user_x1;
        String user_y1;
        int x0;
        int y0;
        int x1;
        int y1;
        boolean turn_completed = false;
        String destination_coordinate;
        ArrayList<String> valid_x = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K"));
        ArrayList<String> valid_y = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"));
        int[] y_coordinates = new int[]{0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        HashMap<String, Integer> x_coordinates = new HashMap<>() {{
            put("A", 0);
            put("B", 1);
            put("C", 2);
            put("D", 3);
            put("E", 4);
            put("F", 5);
            put("G", 6);
            put("H", 7);
            put("I", 8);
            put("J", 9);
            put("K", 10);
        }};

        System.out.println(welcome_message);
        while (true) {
            attacker_turn = !attacker_turn;
            if (attacker_turn) {
                start_turn_message = "Attacker's turn!";
            } else {
                start_turn_message = "Defender's turn!";
            }
            print_board();
            check_victory();
            System.out.println(start_turn_message);
            turn_completed = false;
            while (!turn_completed) {
                System.out.println("Select piece to move. Format: x y Example: F 9\n ");

                //GET AND PARSE USER INPUT
                user_x0 = input.next();
                user_y0 = input.next();
                if (valid_x.contains(user_x0)) {
                    x0 = x_coordinates.get(user_x0);

                    if (valid_y.contains(user_y0)) {
                        y0 = y_coordinates[Integer.parseInt(user_y0)];

                        //CHECK USER SELECTED VALID PIECE
                        if (valid_selection(x0, y0)) {
                            System.out.println("Select destination. Format: x y Example: C 9\n ");

                            //GET AND PARSE DESTINATION
                            user_x1 = input.next();
                            user_y1 = input.next();
                            if (valid_x.contains(user_x1)) {
                                x1 = x_coordinates.get(user_x1);

                                if (valid_y.contains(user_y1)) {
                                    y1 = y_coordinates[Integer.parseInt(user_y1)];
                                    destination_coordinate = Integer.toString(x1) + Integer.toString(y1);

                                    //CHECK TO SEE IF MOVE IS VALID
                                    ArrayList<String> valid_moves = get_valid_move(x0, y0);
                                    if (valid_moves.contains(destination_coordinate)) {
                                        move(x0, y0, x1, y1);
                                        captures(x1, y1);
                                        turn_completed = true;
                                    } else {
                                        System.out.println("Invalid move");
                                    }
                                } else {
                                    System.out.println("Invalid destination y");
                                }
                            } else {
                                System.out.println("Invalid destination x");
                            }
                        } else {
                            System.out.println("Invalid selection");
                        }
                    } else {
                        System.out.println("Invalid origin y");
                    }
                } else {
                    System.out.println("Invalid origin x");
                }
            }
        }
    }
}