# Secure Linked Data (SLD)

SLD represents a flexible and dynamic mechanism for securely storing and efficiently querying RDF datasets. By employing an encryption strategy based on Functional Encryption (FE), in which data access is enforced by the cryptographic approach itself, we allow for fine-grained access control over encrypted RDF data while at the same time reducing the administrative overhead associated with access control management. Present repository contains a prototypical Java implementation of proposed approach which was used for conducting the experiments reported in our ESWC'17 submission.

The encryption schemes used in our implementation are based on the Java Pairing Based Cryptography Library(jPBC): 
   http://gas.dia.unisa.it/projects/jpbc/
You need to add jpbc to your local maven repository in order to run SLD (see [jPBC Build How-To](http://gas.dia.unisa.it/projects/jpbc/buildHowto.html#.V0qc0r_mmiw) for more information).

For more information on SLD, see the project homepage:
   https://aic.ai.wu.ac.at/comcrypt/sld/

### License ###

Our protoype is licensed under the [GNU Lesser General Public License v3](https://www.gnu.org/licenses/lgpl.html). 

### Authors ###

* Simon Steyskal <simon.steyskal@wu.ac.at>
* Javier D. Fernandez <jfernand@wu.ac.at>
* Sabrina Kirrane <sabrina.kirrane@wu.ac.at>
* Axel Polleres <axel.polleres@wu.ac.at>

### Acknowledgements ###

Supported by the Austrian Science Fund (FWF): M1720- G11, and the Vienna Science and Technology Fund (WWTF) project ICT12-15
