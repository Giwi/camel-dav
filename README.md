## camel-dav
This is a Dav client based on [Sardine](https://code.google.com/p/sardine/) and largely inspired by camel-ftp.

# It is still under developement and do not work completely !!!

* minimalist configuration
* no recursive crawl
* Producer :
    * write files ok
    * not recursive
* Consumer :
    * polling ok
    * not recursive
    * The consumer will by default leave the consumed files untouched on the remote FTP server. You have to configure it explicitly if you want it to delete the files or move them to another location. For example you can use delete=true to delete the files, or use move=.done to move the files into a hidden done sub directory.
    * move after rean ok (with the limitation above)
