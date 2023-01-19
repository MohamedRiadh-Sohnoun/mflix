Get Started
-----------

In the finished version of the MFlix application, users will be able to:

- Perform movie searches with the following criteria:

  - cast members
  - genre types
  - text matches in title or description
  - country of origin

- Create faceted filters on movie searches
- Create an account and manage their preferences
- Leave comments on their favorite (or least favorite) movies

... among other features, typical of any movie streaming service.

Project Structure
~~~~~~~~~~~~~~~~~

MFlix is composed of two main components:

- *Frontend*: All the UI functionality is already implemented, which
  includes the built-in React application.

- *Backend*: *Java Spring Boot* project that provides the necessary service to
  the application. The code is managed by a Maven project definition file.

The unit tests in ``src/tests/java/mflix/api/daos`` will test these database
access methods directly, without going through the API.

The UI will run these methods as part of the integration tests, and therefore
they are required for the full application to be running.

The API layer is fully implemented, as is the UI. By default the application
will run on port 5000, but if you need it to run on a port other than 5000, you
can edit the ``index.html`` file in the ``build`` directory to modify the value of
**window.host**. You can find ``index.html`` in the
``src/main/resources/build`` directory.


Database Layer
~~~~~~~~~~~~~~

We will be using *MongoDB Atlas*, MongoDB's official Database as a Service (DBaaS).


Local Environment Dependencies
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

There are two main system dependencies in my project:


1. Java 1.8

2. Maven


Java Project (MFlix) Installation
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The ``mflix`` project is supported by a `Maven` POM file that deals with all the
dependencies required, as well as providing the ``test`` and ``run`` commands
to control our project. This means that you can run all the tests and deploy
the ``mflix`` backend from the command line with `Maven`.


.. code-block:: sh

  $ ls
  mflix README.rst
  $ cd mflix
  $ ls
  src pom.xml data


Running the Application
~~~~~~~~~~~~~~~~~~~~~~~

In the ``mflix/src/main/resources`` directory you can find a file called
``application.properties``.

Open this file and enter your *Atlas SRV* connection string as directed in the
comment. This is the information the driver will use to connect. Make sure
**not** to wrap your *Atlas SRV* connection between quotes::

To run MFlix, run the following command:

.. code-block:: sh

  cd mflix
  mvn spring-boot:run

And then point your browser to `http://localhost:5000/ <http://localhost:5000/>`_.


Running the Unit Tests
~~~~~~~~~~~~~~~~~~~~~~

To run the unit tests, you will use ``JUnit``. use the following command:

.. code-block:: sh

  cd mflix
  mvn -Dtest=<TestClass> test

For example to run the ConnectionTest test your shell command will be:

.. code-block:: sh

  cd mflix
  mvn -Dtest=ConnectionTest test

