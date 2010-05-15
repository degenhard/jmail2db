=========
jmail2db
=========

:author: Peter Heise (http://koprolalie.com)
:license: MIT-license / see LICENSE file for more


About
=====
jmail2db is a java commandline application to save emails in a sqlitedb.


Usage
=====

::

	--type pop3, pop3s, imap, imaps
 	--username username
 	--password password
 	--mailserver mailserver
 	--readwrite (optional, if not set is readonly on mailserver)
 	--dbfile filename (optional, default is 'emails.db')


Dependencies
=====

* javamail (http://java.sun.com/products/javamail/downloads/index.html)
* SqliteJDBC (http://www.zentus.com/sqlitejdbc/)

