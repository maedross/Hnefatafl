Feature: Startup

  As a user
  I want to start the game and see the default board state
  so I can immediately play the game

  Rule: At the start of the game, the board matches the default setup
    Scenario: I start the game
      Given the menu
      When I start the game
      Then the game board state is "setupResult"

  Rule: The first player is the attacker
    Scenario: I start the game
      Given the menu
      When I start the game
      Then it is the attacker's turn
