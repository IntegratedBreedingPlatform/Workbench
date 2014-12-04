# Overview

This is the IBPWorkbench web development source folder. Web development differs from Java development in that we often want quick redeployments to the browser to test our changes. We also want use of pre-processing tools such as Sass. The tools and code structure here allow for both of these.

# Source Structure

Code is built from the source folder (this folder) to `../webapp/WEB-INF`. We check both the source and the built files in, allowing Java developers to get started right away without having to install node or gulp, and do any special steps to build.

Do **NOT** edit files under `/WEB-INF/pages` or `/WEB-INF/static` directly. These will be overwritten by their source files in this folder whenever a build is run.

# Building

## Maven

When an ordinary Maven build is run, a clean and build is trigger through the use of a maven-node-gulp plugin. This will run a clean, deleting the `/WEB-INF/pages` and `/WEB-INF/static` folders and will rebuild everything from scratch. The WEB-INF folder will then be included in the war file that can be deployed to Tomcat.

## Gulp

To use the gulp tasks directly, you need to install Node and run `npm install` in this directory to download the required packages. You can then run the Gulp build task directly with `gulp build`. There are also a number of other tasks that you can run manually (that will be included in a build):

* `clean` - removes `/WEB-INF/static` and `/WEB-INF/pages`
* `build` - runs `js`, `sass`, `images`, `html` and `fonts`. Running with the `--prod` flag will minify JS and CSS.
* `fonts` - copies font files to the build directory
* `html` - copies the contents of the src pages folder to the build folder
* `images` - compresses the images and copies them to the built images folder
* `js` - will JSHint any non-library code, and copy all JavaScript files (libraries too) to the build folder. Running with the `--prod` flag will minify files.
* `sass` - compiles scss files into css, adds vendor prefixes and copies to the build folder. Running with the `--prod` flag will minify files.
* `test` - runs the JavaScript tests (IN PROGRESS)
* `watch` - will invoke the appropriate task when files in the `src` folder change

Running `gulp` without a specified task will run the `build` task.

## Building for Production

Before releasing, we should run a production build. You can run `gulp build --prod`, or specify the `--prod` argument in the gulp build configuration in the pom.xml, and run a Maven build. This will minify files ready for release.

# Development

When developing in a web environment, it is useful to not have to re-build and deploy war files every time you want to test a change. We have added tools to enable this. To enable this, call:

`gulp watch --env=property`

where `property` is a property defined in the `gulp.properties` file specifying the location of your Tomcat installation. This will watch your source folder for changes, and as you develop, will trigger the correct tasks and copy files over the build folder as necessary. In addition to this, **changed files will be copied directly into the exploded IBPWorkbench folder in your Tomcat webapps directory**, allowing you to refresh your browser and see changes without redeploying.

In addtion to this, there are a few things to note about developing in this environment:

* We use Sass, not CSS, which you can find in the sass folder
* We use the auto-prefixer plugin to automatically add vendor prrefixes to our CSS. This happens at build time, so we don't have to specify them ourselves in our Sass files. For more information and to check if your rule is supported, see the [website](https://github.com/postcss/autoprefixer-core)
* We use an image minification library to compress our images on build
* JS libraries should be included already minified in the `js/lib` folder
