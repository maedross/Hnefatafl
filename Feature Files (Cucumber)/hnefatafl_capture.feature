Feature: Capture

  As a user
  I want to capture enemy pieces
  so I can weaken my opponent

  Background:
    Given a game board of "captureBackground"

    Rule: The king is captured when surrounded on 4 sides by attackers
      Scenario: Capture a king not on an edge
        When the attacker moves from 9, 6 to 5, 6
        Then the game board state is "captureKingFour"

    Rule: Normal pieces are captured when surrounded by enemy pieces on two opposite sides
      Scenario: Capture a normal piece
        When the attacker moves from 9, 6 to 9, 9
        Then the game board state is "captureNormal"

    Rule: Hostile squares act as enemy pieces for capturing pieces
      Scenario: capture piece against hostile square
        When the attacker moves from 9, 6 to 9, 1
        Then the game board state is "captureHostile"

    Rule: Normal pieces are captured by the shield wall formation
      Scenario: Capture a group of pieces with shield wall
        When the attacker moves from 4, 5 to 4, 1
        Then the game board state is "captureShieldWall"

    Rule: The king is captured when on the edge of the board and surrounded on three sides by attackers
      Scenario: Capture a king on the edge
        When the attacker moves from 4, 5 to 2, 5
        Then the game state is "captureKingThree"