# ONI Save Parser

The ONI Save Parser is a Kotlin/WASM web app service designed for
showing Oxygen Not Included (ONI) save games.

To read the save game files, it utilizes a Kotlin port
of RoboPhred's [oni-save-parser](https://github.com/RoboPhred/oni-save-parser).

It's only for reading and analyzing saves.
If you are looking for editing and saving files, please use [Duplicity](https://robophred.github.io/oni-duplicity/#/).

## Archival Note

This repository is no longer being updated as it is not practical to continue developing the save parser at this time. The primary challenge is that key information from the save files cannot be extracted without a deeper understanding of the underlying randomization process in the ONI files.

When I ported the save parser to Kotlin to develop a replacement for ToolsNotIncluded, I wasn't aware that extracting all the necessary Geyser information would be more complicated than anticipated, especially compared to how easily a simple mod can achieve this.

For further development, please refer to [mapsnotincluded.org](https://github.com/barratt/mapsnotincluded.org), which is the preferred path forward.

## Acknowledges

I would like to express my sincere appreciation to RoboPhred for developing
the original oni-save-parser and to Bryan Gonzalez for his C# port.
Additionally, special thanks are owed to [SGT_Imalas](https://github.com/Sgt-Imalas) for
providing valuable guidance on handling specialties in the save game files.

## License

This project is licensed under the GNU Affero General Public License (AGPL),
ensuring the community's freedom to use, modify, and distribute the software.
