# MOVED ON CODEBERG

## how to use?
**for resourcepack creators:**
you just need to put in the root of your resourcepack a file named "RPD.txt", like this:
<img width="1304" height="448" alt="immagine" src="https://github.com/user-attachments/assets/13953e45-c946-418a-b306-61fe561fa4c8" />

and in each line of the file you need to insert the mods IDs, like this:
```
modmenu
polytone
other mod IDs...
```
also we have implemented the OR operator  `||`
```
iris||optifine
```
this will ensure that the user have either iris or optifine installed

## how to build

like anyother mod you just need to clone the repository on your IDE and run the gradle task "build"
