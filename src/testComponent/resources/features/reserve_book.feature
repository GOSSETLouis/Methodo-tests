Feature: Reserve a book

  Scenario: Reserve a book
    Given the user creates the book "Les Misérables" written by "Victor Hugo" with reserved status false
    When the user reserves the book with id 1
    Then the book with id 1 should be reserved
