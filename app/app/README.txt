Usage:
Create a new account or login on the opening screen.
Explore the app upon logging in. The lower navigation bar allows you to:
 go to explore page (search)
 create a new conspiracy board (plus)
 go home to local boards (home)
 go to profile page (guest icon)

 The board editing page is the most fully developed part of the program.
 Here you can add images, give them a title, and connect them with red lines using the pencil icon.

 Known bugs:
 Several buttons have not been implemented yet.
 Dragging/moving objects around the board can be clunky at times. We've discovered we need a more custom implementation of the standard function detecttransformgestures() to get the exact gesture sensors we need.

 Change Log:
 Icons have been changed in the board editing page to be more clear on what is going on.
 A zoom percent on the board editing page has been added as well as an indicator for what tool is currently being used.
 A way to label images on the board editing page has been added, with preparation to make it possible to view text beneath images or have plain text with no image associated with it on the board.
 Firebase has been added. While the database is working, no relevant user data is being stored (beyond account information via fire authentication). So, usage is still extremely limited on the end-user side.
 Colors on various buttons have been changed for a more cohesive experience.
 The style of various labels and buttons have been changed for aesthetic purposes.
 
 Feedback from alpha release:
1. The add page is broken for me, as it is just a blank screen. I won't give any feedback for it because of this, but i'm sure its great.
    We could not replicate this bug on our devices.
2. Are you going to have the ability to click on your followers and following to see who are in the lists? I know that is very prevalent on social media.
    We will add this once the database is more finished.
3. Also, I am not sure what the change picture button does, as it just brings me back to the main screen.
    Change picture will allow the user to swap to a new image instead of the weird default one once the database is more finished.
4. Overall the UI is pretty good. I would probably change the colors, but other than that it looks nice. The navigation is easy to follow, but I would recommend changing the order of the bottom navigation icons. Having search, then add, and then home feels weird.
    Colors have been adjusted.
5. Explore page layout is functional, but would be more usable with the addition of more visible organization or visual hierarchy.
    Need Database still.
6. In the create section, it takes some trial and error to determine what each of the tools do. New users would be able to start using it more quickly with tooltips or a short tutorial.
    Display added for tool being used.
7. Some pending data can get lost in the creation menu. To reproduce this, try changing your device to landscape after adding an image to the canvas.
    Landscape mode is no longer being used since it's not common in social media apps. (And again a database issue)



