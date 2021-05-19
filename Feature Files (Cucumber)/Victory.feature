Feature: Victory

  As a user
  I want to make moves that result in a victory for one side
  So I can win the game

  Rule: The attacker wins if they capture the king
    Scenario: Capturing a king not on an edge
      Given a game board of "victoryKingCaptureInterior"
      When the attacker moves from 1, 1 to 1, 1
      Then the attacker wins

    Scenario: Capturing a king on an edge
      Given a game board of "victoryKingCaptureEdge"
      When the attacker moves from 1, 1 to 1, 1
      Then the attacker wins

  Rule: The defender wins if they move the king to a corner
    Scenario: Escape king to top-left corner
      Given a game board of "victoryKingEscapeTopLeft"
      When the defender moves from 1, 3 to 1, 1
      Then the defender wins

    Scenario: Escape king to bottom-right corner
      Given a game board of "victoryKingEscapeBottomRight"
      When the defender moves from 11, 3 to 11, 11
      Then the defender wins

  Rule: Attacker wins if no defending pieces can reach the board edge
    Scenario: Blockade the defender
      Given a game board of "victoryBlockade"
      When the attacker moves from 8, 7 to 7, 7
      Then the attacker wins

  Rule: Defender wins if they have constructed an edge fort
    Scenario: Finish an edge fort
    Given a game board of "victoryEdgeFort"
    When the defender moves from 2, 11 to 3, 11
    Then the defender wins

  Rule: If a player cannot move, they lose
    Scenario: Defender surrounds attacker
      Given a game board of "victoryTrapAttacker"
      When the defender moves from 5, 3 to 4, 3
      Then the defender wins
    Scenario: Attacker surrounds defender
      Given a game board of "victoryTrapDefender"
      When the attacker moves from 5, 3 to 4, 3
      Then the attacker wins
    Scenario: Defender captures last attacker piece
      Given a game board of "victoryEliminateAttacker"
      When the defender moves from 3, 4 to 3, 3
      Then the defender wins