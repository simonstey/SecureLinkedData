# Secure Linked Data (SLD)

## Overview ##

SLD represents a flexible and dynamic architecture for securely storing and maintaining RDF datasets. By employing a two-layered encryption strategy based on both Ciphertext-Policy Attribute-based Encryption (CP-ABE) and Functional Encryption (FE), we allow for fine-grained and revocable access control over encrypted RDF data based on arbitrary triple patterns. The proposed approach allows for the flexible enforcement of access control using triple patterns and at the same time reduces the overhead associated with querying encrypted data. Present repository contains a prototypical Java implementation of a **Policy Tier** and **Data Tier** which were used for conducting the experiments reported in our ISWC'16 submission.

The encryption schemes used in our implementation are based on the Java Pairing Based Cryptography Library(jPBC): 
   http://gas.dia.unisa.it/projects/jpbc/

For more information on SLD, see the project homepage:
   https://aic.ai.wu.ac.at/comcrypt/sld/

### License ###

Our protoype is licensed under the [GNU Lesser General Public License v3](https://www.gnu.org/licenses/lgpl.html). 

### Authors ###

* Simon Steyskal <simon.steyskal@wu.ac.at>
* Javier D. Fernandez <jfernand@wu.ac.at>
* Sabrina Kirrane <sabrina.kirrane@wu.ac.at>

### Acknowledgements ###

Supported by the Austrian Science Fund (FWF): M1720- G11, and the Vienna Science and Technology Fund (WWTF) project ICT12-15
