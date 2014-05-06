Norm
=======

Norm is an utility built on top of Anorm. It's not an ORM with a lot of complex things.

Why
===============

1. Becouse repeating yourself is boring. For simple cases, when you create 2 or 3 classes(models?) to access the database, you'll end up writting a lot of the same things.
2. Becouse to learn even a new idiomatic way to write the same old sql is not a greate innovation

Should I use it?
================

1. If you're writting a very complicated model (joins, inheritance,...) - for now - No
2. If you're not comfortable with unchecked type - No
3. If you want transaction between models - for now - No
4. If you have a lot of simple models, simple access patterns and don't want to learn a new way to access SQL dbs - yes
