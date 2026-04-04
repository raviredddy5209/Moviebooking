/**



Click seat → LOCK seat (API)
Unselect seat → UNLOCK seat (API)
Click Confirm → BOOK seats (API)

Click seat
onSeatClick()
Calls:
/lock OR /unlock
Reload seats → UI updates

FINAL FLOW (PRO LEVEL)
Click → /lock
Deselect → /unlock
Book → /book
Auto-expire → scheduler

updateseat(ui) instead of loadseats() for color canbe used
 

“Implemented seat locking with ownership validation and auto-expiry to prevent race conditions in concurrent bookings.”










*/