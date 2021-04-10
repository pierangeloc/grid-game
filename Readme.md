# ZIO Laminar Game engine

A simple ScalaJs (*) project to create grid-based games (Tetris, Snake, etc...).

The initial setup has been done starting from (https://github.com/shadaj/create-react-scala-app.g8) that provides a working solution based on webpack.

### How it's done
The UI is built using [Laminar](https://laminar.dev/), ahe grid illumination is done by driving different styles provided by the amazing [Tailwind](https://tailwindcss.com/) library. Using Laminar's `Var` and `Signal` it's really straightforward to push different values to drive styles.

Why use Laminar? I was impressed by thw work of [Kit Langton](https://twitter.com/kitlangton) to [visulalize](https://zio.surge.sh/) ZIO combinators. Looking at the code I found it very straightforward and easy to understand,so I decided to give it a try. The interaction with ZIO and ZIO Streams can work seamlessly

The whole application is bootstrapped as a [ZIO](https://zio.dev/) app, which specifies a title and a grid size. To power the game flow it's only required to provide a layer to create a `GameEngine`, which defines 2 ZIO streams, one for the grid state and one for the score. By providing different layers we create different games.

<sub>(*) I'm not a front end developer and every time I have to create a FE project I have to relearn some stuff, so this project might not follow some best practices from a FE point of view. What I try to do is make a visual experiment of how to use functional techniques.</sub>

### Run it locally
To run it locally:

```
sbt frontend/fastOptJS::startWebpackDevServer
```

the page is running on [http://localhost:8080/](http://localhost:8080/)