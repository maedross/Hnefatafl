Feature: Movement

  As a user
  I want the pieces to move correctly
  so I can play a game

  Background:
  Given a game board of "movementBackground"

  Rule: A piece can move horizontally or vertically along the board
    Scenario: Move a piece vertically
      When the attacker moves from 4, 1 to 4, 4
      Then the game board state is "movementVertical"
    Scenario: Move a piece horizontally
      When the attacker moves from 4, 1 to 2,1
      Then the game board state is "movementHorizontal"

  Rule: A piece cannot move past another piece
    Scenario: Try to jump over a piece over another piece
      When the attacker moves from 4, 1 to 8, 1
      Then I must make a different move

  Rule: Only the king can move onto a hostile square
    Scenario: Move the king to the center
      When the defender moves from 6, 1 to 6, 6
      Then the game board state is "movementHostile"
    Scenario: Try to move a non-king piece to the center
      When the attacker moves from 4, 1 to 1, 1
      Then the attacker must make a different move
