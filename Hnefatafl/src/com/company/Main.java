package com.company;
import java.io.*;
import java.util.*;
import static java.util.Map.entry;

/*TO IMPLEMENT:
- OOP
- SHIELD WALL
--USE IS ENEMY AND IS FRIENDLY INSTEAD OF KEEPING TRACK OF TURN/PIECE
- DEBUG
- ATTACKER SURROUND VICTORY CONDITION
--DEPTH-FIRST SEARCH OR OTHER PATHFINDING ALGORITHM
- DEFENDER EDGE FORT VICTORY CONDITION
- BOARD STATE REPETITION ILLEGAL
- MAIN MENU WITH NEW GAME, SAVE GAME, LOAD GAME, RULES
- USER PROFILES WITH WIN/LOSS ATTACKER/DEFENDER STATS
- ARTIFICIAL INTELLIGENCE
- SAVE BOARD AS FILE
*/

public class Main {

    private static class Coordinate {
        int x, y;
        private Coordinate (int cx, int cy) {
            x = cx;
            y = cy;
        }
    }

    private static class Board {
        static Space[][] board = new Space[11][11];

        private Board (String filename) throws FileNotFoundException {
            Scanner load_board = new Scanner(new File(filename));
            for (int j=0; j<11; j++) {
                for (int i=0; i<11; i++) {
                    int encoding = load_board.nextInt();
                    switch (encoding) {
                        default:
                            break;
                        case 0:
                            board[i][j] = new NormalSpace(i, j);
                            board[i][j].occupant = null;
                            break;
                        case 1:
                            board[i][j] = new HostileSpace(i, j);
                            board[i][j].occupant = null;
                        case 2:
                            board[i][j] = new NormalSpace(i, j);
                            board[i][j].occupant = new Defender(i, j, board[i][j]);
                            break;
                        case 3:
                            board[i][j] = new NormalSpace(i, j);
                            board[i][j].occupant = new Attacker(i, j, board[i][j]);
                            break;
                        case 4:
                            board[i][j] = new NormalSpace(i, j);
                            board[i][j].occupant = new King(i, j, board[i][j]);
                            break;
                        case 5:
                            board[i][j] = new HostileSpace(i, j);
                            board[i][j].occupant = new King(i, j, board[i][j]);
                    }
                }
            }
            for (int j=0; j<11; j++) {
                for (int i=0; i < 11; i++) {
                    try {
                        board[i][j].left = board[i-1][j];
                    } catch (Exception e) {
                        board[i][j].left = null;
                    }
                    try {
                        board[i][j].right = board[i+1][j];
                    } catch (Exception e) {
                        board[i][j].right = null;
                    }
                    try {
                        board[i][j].up = board[i][j-1];
                    } catch (Exception e) {
                        board[i][j].up = null;
                    }
                    try {
                        board[i][j].down = board[i][j+1];
                    } catch (Exception e) {
                        board[i][j].down = null;
                    }
                }
            }
        }

        private void print () {
            System.out.println("    A  B  C  D  E  F  G  H  I  J  K ");
            for (int j = 0; j < 11; j++) {
                int row = j + 1;
                if (row<10) {
                    System.out.print(row + "  ");
                } else {
                    System.out.print(row + " ");
                }
                for (int i = 0; i < 11; i++) {
                    System.out.print(board[i][j]);
                }
                if (j<9) {
                    System.out.print("  " + row);
                } else {
                    System.out.print(" " + row);
                }
                System.out.print("\n");
            }
            System.out.println("    A  B  C  D  E  F  G  H  I  J  K ");
        }

        private static class Interpreter {
            String valid_x = "ABCDEFGHIJK";
            String valid_y = "1234567891011";
            private final Map<String, Integer> x_coordinates = Map.ofEntries(entry("A", 0),
                    entry("B", 1), entry("C", 2), entry("D", 3), entry("E", 4), entry("F", 5),
                    entry("G", 6), entry("H", 7), entry("I", 8), entry("J", 9), entry("K", 1));

            private Interpreter (Board board) {}

            private boolean check_x (String x) {
                return valid_x.contains(x);
            }

            private boolean check_y (String y) {
                return valid_y.contains(y);
            }

            private int convert_x (String x) {
                return x_coordinates.get(x);
            }

            private int convert_y (String y) {
                return Integer.parseInt(y) - 1;
            }

            private boolean check_origin (int x, int y, boolean turn) {
                if (turn && board[x][y].occupant instanceof Attacker) {
                    return true;
                } else return !turn && (board[x][y].occupant instanceof Defender ||
                        board[x][y].occupant instanceof King);
            }

            private boolean check_destination (Coordinate dest, Piece piece) {
                ArrayList<Coordinate> valid_dest = piece.available_destinations();
                assert valid_dest != null;
                return valid_dest.contains(dest);
            }

            private Coordinate get_coordinate (boolean msg_select) {
                String message;
                if (msg_select) message = "Select piece to move. Format: x y Example: F 9\n";
                else message = "Select destination. Format: x y Example: C 9\n";
                Scanner input = new Scanner(System.in);
                System.out.println("Select piece to move. Format: x y Example: F 9\n ");
                int x = 0, y = 0;
                String user_x = input.next();
                if (check_x(user_x)) {
                    x = convert_x(user_x);
                } else {
                    System.out.println("Invalid x value");
                    this.get_coordinate(msg_select);
                }
                String user_y = input.next();
                if (check_y(user_y)) {
                    y = convert_y(user_y);
                } else {
                    System.out.println("Invalid y value");
                    this.get_coordinate(msg_select);
                }
                return new Coordinate(x, y);
            }

            private void get_move(boolean turn) {
                Coordinate o_coord = get_coordinate(true);
                if (!check_origin(o_coord.x, o_coord.y, turn)) {
                    System.out.println("Invalid origin");
                    this.get_move(turn);
                }

                Coordinate d_coord = get_coordinate(false);
                Piece mover = board[o_coord.x][o_coord.y].occupant;
                if (check_destination(d_coord, mover)) {
                    mover.move(d_coord.x, d_coord.y);
                } else {
                    System.out.println("Invalid move.");
                }
            }
        }

        private abstract class Space {
            int x,y;
            String gui;
            Space left, right, up, down;
            Piece occupant;
            Coordinate coordinate;
            boolean corner;
            boolean is_edge;
            int edge = -1;
            int u_edge = 0;
            int r_edge = 1;
            int d_edge = 2;
            int l_edge = 3;

            private Space (int board_x, int board_y) {
                x = board_x;
                y = board_y;
                coordinate = new Coordinate(x, y);
                if ((x == y) && (x == 0 || x == 10) ) corner = true;
                else if (x == 0) edge = l_edge;
                else if (x == 10) edge = r_edge;
                else if (y == 0) edge = u_edge;
                else if (y == 10) edge = d_edge;
                if (edge != -1) is_edge = true;
            }

            private void set_gui () {}

            private void print() {
                System.out.print(gui);
            }
        }

        private class NormalSpace extends Space {
            private NormalSpace(int x, int y) {
                super(x, y);
            }

            private void set_gui() {
                if (occupant instanceof Attacker) gui = "[A]";
                else if (occupant instanceof Defender) gui = "[D]";
                else if (occupant instanceof King) gui = "[K]";
                else gui = "[ ]";
            }
        }

        private class HostileSpace extends Space {
            private HostileSpace(int x, int y) {
                super (x, y);
            }

            private void set_gui() {
                if (occupant instanceof King) gui = "{K}";
                else gui = "{ }";
            }
        }

        private abstract class Piece {
            int x, y;
            Space space;

            private Piece (int start_x, int start_y, Space start_space) {
                x = start_x;
                y = start_y;
                space = start_space;
            }

            private ArrayList<Coordinate> available_destinations () {
                return null;
            }

            private void move(int x, int y) {
                space.occupant = null;
                this.x = x;
                this.y = y;
                space = board[x][y];
                space.occupant = this;
            }
            
        }

        private class Attacker extends Piece {
            private Attacker (int x, int y, Space space){
                super(x, y, space);
            }

            private boolean is_enemy(Piece piece) {
                return piece instanceof Defense;
            }

            private ArrayList<Coordinate> available_destinations () {
                ArrayList<Coordinate> dest_coords = new ArrayList<>();
                for (Space curr=space.left; curr!= null; curr=curr.left) {
                    if (curr.occupant != null) break;
                    else if (!(curr instanceof HostileSpace)) {
                        dest_coords.add(space.coordinate);
                    }
                }
                for (Space curr=space.right; curr!= null; curr=curr.right) {
                    if (curr.occupant != null) break;
                    else if (!(curr instanceof HostileSpace)) {
                        dest_coords.add(space.coordinate);
                    }
                }
                for (Space curr=space.up; curr!= null; curr=curr.up) {
                    if (curr.occupant != null) break;
                    else if (!(curr instanceof HostileSpace)) {
                        dest_coords.add(space.coordinate);
                    }
                }
                for (Space curr=space.up; curr!= null; curr=curr.up) {
                    if (curr.occupant != null) break;
                    else if (!(curr instanceof HostileSpace)) {
                        dest_coords.add(space.coordinate);
                    }
                }
                return dest_coords;
            }
        }

        private abstract class Defense extends Piece {
            private Defense (int x, int y, Space space) {
                super(x, y, space);
            }

            private boolean is_enemy(Piece piece) {
                return piece instanceof Attacker;
            }
        }

        private class Defender extends Defense {
            private Defender (int x, int y, Space space) {
                super(x, y, space);
            }
        }

        private class King extends Defense {
            private King (int x, int y, Space space) {
                super(x, y, space);
            }
        }
    }

    private static final String welcome_message = "Welcome to Hnefatafl! The goal of the game is to capture the king by surrounding him " +
            "\non all 4 sides if you are the attacker, or to move your king to a corner and escape " +
            "\nif you are the defender. Pieces move in straight lines, like rooks in chess; they " +
            "\ncannot move past other pieces, and only the king can move onto the hostile squares " +
            "\n{ }. Capture enemy pieces by surrounding them on two opposite sides. Be careful; both" +
            "\n sides can use hostile squares to capture pieces!\n\n";

    private static final int ATT = 0;
    private static final int DEF = 1;
    private static boolean att_turn = true;
    private static int victory = 0;

    //RETURNS TRUE IF SANDWICHED BETWEEN ENEMY PIECES AND/OR HOSTILE SQUARE
    private static boolean piece_sandwiched(int x, int y) {
        boolean def_turn = !att_turn;
        if (x == 0 || x == 10) {
            return (is_enemy(x, y-1, def_turn) && is_enemy(x, y+1, def_turn));
        }
        if (y == 0 || y == 10) {
            return is_enemy(x-1, y, def_turn) && is_enemy(x+1, y, def_turn);
        }
        return (is_enemy(x-1, y, def_turn) && is_enemy(x+1, y, def_turn))
                || (is_enemy(x, y-1, def_turn) && is_enemy(x, y+1, def_turn));
    }

    private static void captures(int x, int y) {
        int[] enemy_coords = new int[4];
        int edge = 0;
        int direction1, direction2;
        if (att_turn)
            attacker_captures(x, y);
        else
            defender_captures(x, y);

        if (x == 0) {
            enemy_coords = potential_shield_wall(x, y, left);
            edge = left;
        } else if (x == 10) {
            enemy_coords = potential_shield_wall(x, y, right);
            edge = right;
        } else if (y == 0) {
            enemy_coords = potential_shield_wall(x, y, up);
            edge = up;
        } else if (y == 10) {
            enemy_coords = potential_shield_wall(x, y, down);
            edge = down;
        }
        direction1 = get_direction(x, y, enemy_coords[0], enemy_coords[1]);
        direction2 = get_direction(x, y, enemy_coords[2], enemy_coords[3]);
        if (enemy_coords[0] != 0 || enemy_coords[1] != 0) {
            if (surrounded_shield_wall(enemy_coords[0], enemy_coords[1], edge, direction1)){
                System.out.println("You made a shield wall capture!");
                shield_wall_capture(enemy_coords[0], enemy_coords[1], direction1);
            }
        }
        if (enemy_coords[2] != 0 || enemy_coords[3] != 0) {
            if (surrounded_shield_wall(enemy_coords[2], enemy_coords[3], edge, direction2)) {
                System.out.println("You made a shield wall capture!");
                shield_wall_capture(enemy_coords[2], enemy_coords[3], direction2);
            }
        }
    }

    private static int get_direction(int x, int y, int ex, int ey) {
        if (x != ex) {
            if (x - ex > 0) {
                return left;
            } else {
                return right;
            }
        } else {
            if (y - ey > 0) {
                return up;
            } else {
                return down;
            }
        }
    }

    private static void attacker_captures(int x, int y) {
        //CAPTURE DEFENDER PIECE
        String capture_message = "You captured a Defender piece!";
        //PIECE TO LEFT
        if (x-1 >= 0 && Board[x-1][y].equals(DEFENDER)) {
            if (piece_sandwiched(x - 1, y)) {
                System.out.println(capture_message);
            }
        }
        //PIECE TO RIGHT
        if (x+1 <= 10 && Board[x+1][y].equals(DEFENDER)) {
            if (piece_sandwiched(x + 1, y)) {
                System.out.println(capture_message);
            }
        }
        //PIECE UP
        if (y-1 >= 0 && Board[x][y-1].equals(DEFENDER)) {
            if (piece_sandwiched(x, y - 1)) {
                System.out.println(capture_message);
            }
        }
        //PIECE DOWN
        if (y+1 <= 10 && Board[x][y+1].equals(DEFENDER)) {
            if (piece_sandwiched(x, y + 1)) {
                System.out.println(capture_message);
            }
        }

        //CAPTURE KING
        capture_message = "You captured the King!";
        //KING TO LEFT
        if (x-1 >= 0 && (Board[x-1][y].equals(KING_HOSTILE) || Board[x-1][y].equals(KING_NORMAL))) {
            if (king_surrounded(x - 1, y)) {
                System.out.println(capture_message);
                victory = 1;
            }
        }
        //KING TO RIGHT
        if (x+1 <= 10 && (Board[x+1][y].equals(KING_HOSTILE) || Board[x+1][y].equals(KING_NORMAL))) {
            if (king_surrounded(x + 1, y)) {
                System.out.println(capture_message);
                victory = 1;
            }
        }
        //KING UP
        if (y-1 >= 0 && (Board[x][y-1].equals(KING_HOSTILE) || Board[x][y-1].equals(KING_NORMAL))) {
            if (king_surrounded(x, y - 1)) {
                System.out.println(capture_message);
                victory = 1;
            }
        }
        //KING DOWN
        if (y+1 <= 10 && (Board[x][y+1].equals(KING_HOSTILE) || Board[x][y+1].equals(KING_NORMAL))) {
            if (king_surrounded(x, y + 1)) {
                System.out.println(capture_message);
                victory = 1;
            }
        }
    }

    private static boolean king_surrounded(int x, int y) { //RETURNS TRUE IF THE KING IS SURROUNDED BY ATTACKERS (AND A HOSTILE SQUARE); RETURNS FALSE OTHERWISE
        return (Board[x + 1][y].equals(ATTACKER) || Board[x + 1][y].equals(HOSTILE)) &&
                (Board[x - 1][y].equals(ATTACKER) || Board[x - 1][y].equals(HOSTILE)) &&
                (Board[x][y + 1].equals(ATTACKER) || Board[x][y + 1].equals(HOSTILE)) &&
                (Board[x][y - 1].equals(ATTACKER) || Board[x][y - 1].equals(HOSTILE));
    }

    private static void defender_captures(int x, int y) {
        //CAPTURE ATTACKER PIECE
        String capture_message = "You captured an Attacker piece!";
        //PIECE TO LEFT
        if (x-1 >= 0 && Board[x-1][y].equals(ATTACKER)) {
            if (piece_sandwiched(x - 1, y)) {
                System.out.println(capture_message);
            }
        }
        //PIECE TO RIGHT
        if (x+1 <= 10 && Board[x+1][y].equals(ATTACKER)) {
            if (piece_sandwiched(x + 1, y)) {
                System.out.println(capture_message);
            }
        }
        //PIECE UP
        if (y-1 >0 && Board[x][y-1].equals(ATTACKER)) {
            if (piece_sandwiched(x, y - 1)) {
                System.out.println(capture_message);
            }
        }
        //PIECE DOWN
        if (y+1 <= 10 && Board[x][y+1].equals(ATTACKER)) {
            if (piece_sandwiched(x, y + 1)) {
                System.out.println(capture_message);
            }
        }
    }
    
    //x and y are of root piece to capture
    //edge is which edge of the board we are on - left, right, up, down
    static int left = 8;
    static int right = 4;
    static int up = 2;
    static int down = 1;

    //pass (x,y) of first enemy to take out
    private static void shield_wall_capture(int x, int y, int direction) {
        Board[x][y] = NORMAL;
        if (((direction & left) != 0) && is_enemy(x-1, y, att_turn)){
            shield_wall_capture(x-1, y, direction);
        }
        if (((direction & right) != 0) && is_enemy(x+1, y, att_turn)){
            shield_wall_capture(x+1, y, direction);
        }
        if (((direction & up) != 0) && is_enemy(x, y-1, att_turn)){
            shield_wall_capture(x, y-1, direction);
        }
        if (((direction & down) != 0) && is_enemy(x, y+1, att_turn)){
            shield_wall_capture(x, y+1, direction);
        }
    }

    //x & y are for enemy piece
    private static boolean surrounded_shield_wall (int x, int y, int edge, int direction){
        boolean def_turn = !att_turn;
        if ((edge & left) != 0){
            if (is_enemy(x - 1, y, def_turn)) {
                return false;
            }
            if ((direction & down) != 0) {
                if (Board[x][y+1].equals(NORMAL)) {
                    return false;
                }
                if (is_friend(x, y+1, def_turn)) {
                    return surrounded_shield_wall(x, y+1, edge, direction);
                }
            }
            else if ((direction & up) != 0) {
                if (Board[x][y-1].equals(NORMAL)) {
                    return false;
                }
                if (is_friend(x, y-1, def_turn)) {
                    return surrounded_shield_wall(x, y-1, edge, direction);
                }
            }
        }
        if ((edge & right) != 0) {
            if (is_enemy(x + 1, y, def_turn)) {
                return false;
            }
            if ((direction & down) != 0) {
                if (Board[x][y+1].equals(NORMAL)) {
                    return false;
                }
                if (is_friend(x, y+1, def_turn)) {
                    return surrounded_shield_wall(x, y+1, edge, direction);
                }
            }
            else if ((direction & up) != 0) {
                if (Board[x][y-1].equals(NORMAL)) {
                    return false;
                }
                if (is_friend(x, y-1, def_turn)) {
                    return surrounded_shield_wall(x, y-1, edge, direction);
                }
            }
        }
        if ((edge & up) != 0) {
            if (is_enemy(x, y + 1, def_turn)) {
                return false;
            }
            if ((direction & left) != 0) {
                if (Board[x-1][y].equals(NORMAL)) {
                    return false;
                }
                if (is_friend(x, y, def_turn)) {
                    return surrounded_shield_wall(x, y, edge, direction);
                }
            } else if ((direction & right) != 0) {
                if (Board[x+1][y].equals(NORMAL)) {
                    return false;
                }
                if (is_friend(x, y, def_turn)) {
                    return surrounded_shield_wall(x, y, edge, direction);
                }
            }
        }
        if ((edge & down) != 0) {
            if (is_enemy(x, y - 1, def_turn)) {
                return false;
            }
            if ((direction & left) != 0) {
                if (Board[x-1][y].equals(NORMAL)) {
                    return false;
                }
                if (is_friend(x, y, def_turn)) {
                    return surrounded_shield_wall(x, y, edge, direction);
                }
            } else if ((direction & right) != 0) {
                if (Board[x+1][y].equals(NORMAL)) {
                    return false;
                }
                if (is_friend(x, y, def_turn)) {
                    return surrounded_shield_wall(x, y, edge, direction);
                }
            }
        }
        return true;
    }

    //returns array of adjacent enemy coordinates; x & y are for player piece
    private static int[] potential_shield_wall (int x, int y, int side){
        int[] enemy_coords = new int[4];
        if (side == left || side == right) {
            if (is_enemy(x, y-1, att_turn)) {
                enemy_coords[0] = x;
                enemy_coords[1] = y-1;
            }
            if (is_enemy(x, y+1, att_turn)) {
                enemy_coords[2] = x;
                enemy_coords[3] = y+1;
            }
        } else if (side == up || side == down) {
            if (is_enemy(x - 1, y, att_turn)) {
                enemy_coords[0] = x - 1;
                enemy_coords[1] = y;
            }
            if (is_enemy(x + 1, y, att_turn)) {
                enemy_coords[2] = x + 1;
                enemy_coords[3] = y;
            }
        }
        return enemy_coords;
    }

    private static boolean check_victory() {
        return Board[0][0].equals(KING_HOSTILE) || Board[0][10].equals(KING_HOSTILE) ||
                Board[10][0].equals(KING_HOSTILE) || Board[10][10].equals(KING_HOSTILE);
    }

    private static void declare_victory(int victory_type)
    {
        print_board();
        switch (victory_type) {
            default:
                System.out.println("Uh-oh! Something went wrong, and a false victory is being declared!");
                break;
            case 1:
                System.out.println("Congratulations! The Attacker won by capturing the king!");
                break;
            case 2:
                System.out.println("Congratulations! The Defender won by escaping with their king!");
                break;
            case 3:
                System.out.println("Congratulations! The Attacker won by cutting off any means of escape to the edges!");
                break;
            case 4:
                System.out.println("Congratulations! The Defender won by forming an edge fort!");
                break;
            case 5:
                System.out.println("Congratulations! The Attacker wins as the Defender is unable to move!");
                break;
            case 6:
                System.out.println("Congratulations! The Defender wins as the Attacker is unable to move!");
                break;
        }
    }


    public void main(String[] args) throws Exception {
        Boolean turn = true; //true = attacker, false = defender
        Board gameboard = new Board("shield-wall.txt");
        Board.Interpreter interpreter = new Board.Interpreter(gameboard);
        String start_turn_message;
        System.out.println(welcome_message);
        while (true) {
            if (att_turn) {
                start_turn_message = "Attacker's turn!";
            } else {
                start_turn_message = "Defender's turn!";
            }
            gameboard.print();
            check_victory();
            System.out.println(start_turn_message);
            interpreter.get_move(turn);

            if (victory != 0) {
                declare_victory(victory);
                System.exit(0);
            }
            att_turn = !att_turn;
        }
    }
}