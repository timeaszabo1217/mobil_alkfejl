# Mobil alkalmazásfejlesztés

Jelen projektben egy Android alapú bútor webshop alkalmazás fejlesztése volt a feladat.

## Feladat szöveges leírása
A projekt célja egy Android alapú bútor webshop fejlesztése, amely lehetővé teszi a felhasználók számára a bútorok böngészését, kosárba helyezését és megrendelését. Az alkalmazás tartalmazza a termékek listáját, részletes termékadatokat, felhasználói értékeléseket és keresési lehetőséget. A felhasználók regisztrálhatnak és bejelentkezhetnek. Az adminisztrátorok kezelhetik a termékeket és rendeléseket. Az alkalmazás fejlesztéséhez Android Studio és Firebase technológiákat alkalmazunk az autentikáció, adatkezelés és szinkronizálás biztosítására.

## Követelmények

### Regisztráció, Bejelentkezés, Kijelentkezés

- **Regisztráció**
  - Új felhasználók e-mail és jelszó megadásával regisztrálhatnak (Firebase Authentication).
  - Jelszó mező csillagozott, e-mail mező helyes billentyűzetet hoz elő.

- **Bejelentkezés**
  - A regisztrált felhasználó bejelentkezhet email, jelszó párossal (Firebase Authentication).
  - Sikeres bejelentkezés után főoldalra navigálás (`Intent`).
  - Értesítés megjelenítése (Firebase Cloud Messaging).

- **Kijelentkezés**
  - A bejelentkezett felhasználó bármikor kijelentkezhet az alkalmazásból.

### Főoldal (Terméklista oldal)

- Bútorok listázása valós időben.
- Keresés név és kategória alapján.
- Szűrés anyag, méret és ár szerint.
- Komplex Firestore lekérdezések (`where`, `orderBy`, `startAt`, `limit`).
- Termék kiválasztása részletes megtekintésre.
- Animáció alkalmazása (pl. lista betöltése, oldalváltás).

### Termék adatlap

- Termékinformációk megjelenítése: név, ár, jellemzők, készlet.
- Termékképek böngészése.
- Vásárlói értékelések megjelenítése.
- "Kosárba" gomb animációval.
- Reszponzív megjelenés különböző kijelzőméreteken.

### Kosár

- Kosár tartalmának megjelenítése és szerkesztése.
- Termék mennyiségének módosítása, törlés.
- Összesített ár és darabszám kalkulálása.

- **Megrendelés véglegesítése**
  - Vendégként: kötelező adatok megadása (név, email, telefon, cím).
  - Regisztráltként: mentett adatok automatikus betöltése vagy új mentése.

- Rendelés mentése Firestore-ba külön szálon (`coroutine` vagy `AsyncTask`).

### Rendeléseim (Felhasználói oldal)

- Korábbi rendelések listázása.
- Rendelés státuszának megjelenítése.
- Firestore lekérdezés `whereEqualTo`-val felhasználói azonosítóra.
- Lista frissítése pl. `onResume` használatával.

### Admin oldal

- **Termékek kezelése (CRUD)**
  - Termékek hozzáadása, módosítása, törlése.

- **Rendelések kezelése**
  - Rendelések megtekintése.
  - Státusz módosítása.
  
- Adatkezelés külön szálon.

### Értesítések és jogosultságok

- **Firebase Cloud Messaging**
  - Értesítés sikeres bejelentkezés és rendelés után.

- **Android Permissions**
  - Értesítések és internet jogosultság kezelése.

### Navigáció, Layout és Animációk

- Navigáció `Intent` segítségével.
- Kétféle layout: `ConstraintLayout` + `ScrollView` vagy `LinearLayout`.
- Reszponzív dizájn, képernyőforgatás utáni korrekt megjelenés.
- Legalább 2 animáció:
  - Kosárba helyezés (átrepülő termék ikon)
  - Listaelemek animációja (`fade-in`, `slide-up`)
