# Pocket Imperium 🚀

**Pocket Imperium** is an adaptation of the strategic space exploration game of the same name. This project delivers a robust and immersive implementation in Java, featuring complete gameplay, a graphical interface developed with JavaFX, and a modular architecture.

---

## Key Features ✨

- **Faithful to the original rules**: 🛸 [Pocket Imperium Rules](http://www.goodlittlegames.co.uk/games/10-pocket-imperium.html)
- **Bots**: 👥 Play with human players or bots opponents (aggressive/friendly strategies).
- **Dynamic board**: 🧩 Highlight possible actions and generate random setups.
- **Save system**: 💾 Resume your game precisely where you left off.
- **Immersive interface**: 🎨 Futuristic design, animations, and intuitive highlights powered by JavaFX.

---

## Screenshots 📸

### Board View 🌌
| ![Board View 1](https://imgur.com/qI7ERqK) | ![Board View 2](https://imgur.com/hPv4LpN) |
|:-----------------------------------------------:|:-----------------------------------------------:|
| **Hexagonal grid during initial deployment**           | **Choosing your commands order**              |
| ![Board View 3](https://imgur.com/undefined) | ![Board View 4](https://imgur.com/oV3JGN9) |
| **Playing the Explore command**                  | **Scoring (Exploit) phase**                     |

### Action Menu 🛠️
| ![Action Menu](https://imgur.com/TBKC7lv) |
|:-----------------------------------------------:|
| **Interactive menu for player actions**        |

### Save System & Settings 💾⚙️
| ![Save System](https://imgur.com/0NvyUwj) | ![Settings]([https://via.placeholder.com/400](https://imgur.com/2o5LWbF)) |
|:------------------------------------------------:|:--------------------------------------------:|
| **Save your current game**                       | **Load a previously saved game seamlessly**          |

---

## Architecture & Design Patterns 🏗️

- **MVC**: 📂 Clear separation of logic, interface, and interactions.
- **State Pattern**: 🔄 Manages different game phases.
- **Strategy Pattern**: 🧠 Adaptive AI with multiple behaviors.
- **Singleton**: 🔒 Ensures a unique instance for central game management.
- **Observer**: 👀 Dynamically synchronizes the model and interface.

---

## Installation 🖥️

1. **Requirements**: Java 11+.
2. **Clone the repository**:
   ```bash
   git clone https://github.com/Flupko/pocket-imperium.git
   ```
3. **Run the application**:
   ```bash
   cd pocket-imperium
   mvn clean install
   mvn javafx:run
   ```

---

## Credits 🙌

- **Developed by**: [Flupko](https://github.com/Flupko).

---

**Enjoy your space adventure! 🌠**
