# Introduction to Web Crawler
Web Crawler scans a site building a sitemap.  It takes the root homepage to
start at as a command line parameter.  Its scans for links on that page within
the same domain and scans them.  Its output is a tab indented one page per
line.

# Building Web Crawler
This version was built in Intelli-J IDEA.  That build system produces one jar
file artifact, “web_crawler.jar.”  That jar file contains all of the dependencies
used.  It is an executable jar file run by the run script “web_crawler.sh.”

I would be possible to build this software either directly on the command
line or with a third-party tool such as Maven or Gradle.

When using Intelli-J, one manual step is involved:
```
	$ cp -v out/artifacts/web_crawler_jar/web_crawler.jar lib/.
	'out/artifacts/web_crawler_jar/web_crawler.jar' -> 'lib/./web_crawler.jar'
```

# Testing Web Crawler
This version includes junit tests: {CommandLineTest, WebCrawlerTest}.
Manual testing may be done on the command line as well:
```
	$ bin/web_crawler.sh --help
    DESCRIPTION:
        Web Crawler will scan the specified site outputing a sitemap.

    USAGE:
        $ <Web Crawler Home>/bin/web_crawler.sh <OPTIONS>
    
            At least one of the following options must be specified.
    
        OPTIONS:
            -h | —help                         Prints help message.
            -u | —usage                        Prints usage instruction.
            -t          <target home page URL>  Scans the site at the given target homepage.
            —target    <target home page URL>  Scans the site at the given target homepage.
…
```

# Running Web Crawler
```
    $ bin/web_crawler.sh -t http://chadwickboggs.com
    http://chadwickboggs.com
    	http://chadwickboggs.com/resume.pdf
    	http://chadwickboggs.com/resume.txt
    	http://chadwickboggs.com/resume.rtf
    	http://chadwickboggs.com/resume.epub
    	http://chadwickboggs.com/resume.docx
    	http://chadwickboggs.com/favicon.ico
    	http://chadwickboggs.com/resume.html
    	http://chadwickboggs.com/resume.pages
    	http://chadwickboggs.com/resume.odt
    	http://chadwickboggs.com/resume.doc
```

# Possible Enhancements to Web Crawler
## Concurrency
This version is single threaded.  One single thread does all of the work.  One
enhancement would be to configurable concurrency, possibly even dynamic
auto-adjusting concurrency and throttling as well.  Multiple pages could be
requested and downloaded concurrently, separate pages could be scanned
concurrently as well as scanning concurrent with downloading.

## Scanning
This version scans blindly unaware of HTML and scripting language specifics.
Scanning could be made HTML aware and scripting language aware to avoid
picking up commented out URL’s and to be able to discover dynamically
generated URL’s as well as script requested URL’s.

## Asynchronous Networking
This version uses traditional request/response blocking I/O.  New I/O,
asynchronous, and event oriented I/O could be added.

## Run Script
This version uses a trivial run script.  It could be improved to provide more
information to the user during error conditions.  Platform portability could be
added as well and man and info pages for Unix.

## Distribution Packaging / Installation
This version uses the simplest approach.  RPM, Yum, Apt packing could be added.

## Testing
This version contains minimal test code.  Test coverage calculation could be 
added.  Test definition languages could be improved with BDD/Cucumber.
Stress tests could be added.

## Build System
This version uses Intelli-J IDEA’s build system only.  A dependency management
and build system such as Maven or Gradle could be added.

## Code Version, Quality, and Safety
This version uses Java version 1.8.  It could be advanced to a newer language
version such as 11 or a different language such as Kotlin.  Third-party frameworks
Could be added to improved code readability, fault tolerance, and quality.

## Logging
This version outputs to stdout and stderr only.  A logging system could be added.
