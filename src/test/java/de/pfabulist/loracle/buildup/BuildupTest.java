package de.pfabulist.loracle.buildup;

import com.google.gson.GsonBuilder;
import de.pfabulist.kleinod.nio.Filess;
import de.pfabulist.loracle.license.Coordinates;
import de.pfabulist.loracle.license.LOracle;
import de.pfabulist.loracle.license.LicenseID;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static de.pfabulist.kleinod.text.Strings.getBytes;
import static de.pfabulist.nonnullbydefault.NonnullCheck._nn;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Copyright (c) 2006 - 2016, Stephan Pfab
 * SPDX-License-Identifier: BSD-2-Clause
 */

@SuppressFBWarnings( { "EQ_COMPARETO_USE_OBJECT_EQUALS" } )
public class BuildupTest {

    @Test
    public void spdx() {
        LOracle lOracle = new LOracle();
        new ExtractSpdxLicensesFromHTML().getLicenses( lOracle );

        assertThat( lOracle.getSingle( "ZPl-1.1" ) ).isPresent();
    }

    @Test
    public void lesserOrLater() {
        LOracle lOracle = new LOracle();
        new ExtractSpdxLicensesFromHTML().getLicenses( lOracle );

        lOracle.addLongName( lOracle.getOrThrowByName( "LGPL-3.0+" ), "GNU Lesser Public License" );

        System.out.println( lOracle.getByName( "GNU Lesser Public License" ) );
    }

    @Test
    public void testApache() {
        LOracle lOracle = new LOracle();
        new ExtractSpdxLicensesFromHTML().getLicenses( lOracle );

        assertThat( lOracle.getByName( "Apache Software License - Version 2.0" ) ).
                isEqualTo( lOracle.getByName( "apache-2.0" ) );

//        javax.servlet:javax.servlet-api:jar:3.1.0:compile
//                [INFO] name:      CDDL + GPLv2 with classpath exception
//                [INFO] long name: Optional.empty
//                [INFO] arti:      Optional.empty
    }

    @Test
    public void testClasspathException() {
        LOracle lOracle = new LOracle();
        new ExtractSpdxLicensesFromHTML().getLicenses( lOracle );
        new ExtractSPDXExceptionsFromHTML( lOracle );

        assertThat( lOracle.getByName( "apache-2.0 with Classpath-exception-2.0" ) ).isPresent();

    }

    @Test
    public void testJavaxServlet() {
        LOracle lOracle = new LOracle();
        new ExtractSpdxLicensesFromHTML().getLicenses( lOracle );
        new ExtractSPDXExceptionsFromHTML( lOracle );

        lOracle.addLicenseForArtifact( new Coordinates( "javax.servlet", "javax.servlet-api", "3.1.0" ),
                                       lOracle.getOrThrowByName( "cddl-1.1 with Classpath-exception-2.0" ) );

        assertThat( lOracle.getByCoordinates( new Coordinates( "javax.servlet", "javax.servlet-api", "3.1.0" ) ) ).isPresent();
    }

    @Test
    public void plus() {
        LOracle lOracle = new LOracle();
        new ExtractSpdxLicensesFromHTML().getLicenses( lOracle );

        assertThat( lOracle.getByName( "GPL-2.0+" ) ).isPresent();

        System.out.println( lOracle.getByName( "GPL-2.0+" ) );
    }

    @SuppressFBWarnings( { "DMI_HARDCODED_ABSOLUTE_FILENAME" } )
    @Test
    public void testBuildup() {

        System.out.println( "----------------------- ");
        System.out.println( "------ spdx ----------- ");
        System.out.println( "----------------------- ");

        LOracle lOracle = new LOracle();
        new ExtractSpdxLicensesFromJSON().go( lOracle );
        new ExtractSPDXExceptionsFromHTML( lOracle );

        System.out.println( "\n\n#licenses " + lOracle.getSingleLicenseCount() + "\n\n");


        lOracle.addUrl( lOracle.getOrThrowByName( "bsd-2-clause" ), "opensource.org/licenses/bsd-2-clause" );
        lOracle.addLongName( lOracle.getOrThrowByName( "agpl-1.0" ), "affero 1.0" );
        lOracle.addLongName( lOracle.getOrThrowByName( "agpl-3.0" ), "affero 3.0" );
        lOracle.addLongName( lOracle.getOrThrowByName( "GPL-2.0 with Classpath-exception-2.0" ), "GPL2 w/ CPE" );
        lOracle.addLongName( lOracle.getOrThrowByName( "isc" ), "isc bsd" );

        // extra
        lOracle.addLongName( lOracle.getOrThrowByName( "lgpl-2.1" ), "GNU Lesser General Public License (LGPL), Version 2.1" ); // extra (LGPL)
        lOracle.addLongName( lOracle.getOrThrowByName( "gpl-3.0" ), "gnu (gpl) 3.0" );
        lOracle.addLongName( lOracle.getOrThrowByName( "gpl-2.0" ), "gnu (gpl) 2.0" );
        lOracle.addLongName( lOracle.getOrThrowByName( "gpl-1.0" ), "gnu (gpl) 1.0" );

        System.out.println( "----------------------- ");
        System.out.println( "------ dejacode ------- ");
        System.out.println( "----------------------- ");

        new ExtractFromDejaCode().go( lOracle );

        System.out.println( "\n\n#licenses " + lOracle.getSingleLicenseCount() + "\n\n");


        lOracle.addUrl( lOracle.getOrThrowByName( "CDDl-1.1 or GPL-2.0 with Classpath-exception-2.0" ), "https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html" );
        lOracle.addUrl( lOracle.getOrThrowByName( "CDDl-1.1 or GPL-2.0 with Classpath-exception-2.0" ), "glassfish.java.net/public/cddl+gpl_1_1" );
        lOracle.addUrl( lOracle.getOrThrowByName( "CDDl-1.0 or GPL-2.0 with Classpath-exception-2.0" ), "https://glassfish.dev.java.net/public/CDDL+GPL.html" );
        lOracle.addUrl( lOracle.getOrThrowByName( "CDDl-1.0 or GPL-2.0 with Classpath-exception-2.0" ), "http://glassfish.java.net/public/CDDL+GPL.html" );
        lOracle.addUrl( lOracle.getOrThrowByName( "CDDl-1.0 or GPL-2.0 with Classpath-exception-2.0" ), "https://glassfish.dev.java.net/nonav/public/CDDL+GPL.html" );

//        lOracle.addUrl( lOracle.getOrThrowByName( "cc0-1.0" ), "repository.jboss.org/licenses/cc0-1.0" );

        // osi license urls
        lOracle.addUrl( lOracle.getOrThrowByName( "mit" ), "http://www.opensource.org/licenses/mit-license.php" );

        // gnu license urls
        lOracle.addUrl( lOracle.getOrThrowByName( "LGPL-3.0+" ), "gnu.org/licenses/lgpl" );

        // fsf license urls
//        lOracle.addUrl( lOracle.getOrThrowByName( "agpl-3.0" ), "www.fsf.org/licensing/licenses/agpl-3.0.html"  );

        // creative commons urls
        lOracle.addUrl( lOracle.getOrThrowByName( "cc0-1.0" ), "http://creativecommons.org/publicdomain/zero/1.0/" );

        // new licenses
        lOracle.newSingle( "closed", false );
        lOracle.addLicenseForArtifact( Coordinates.valueOf( "aopalliance:aopalliance:1.0" ), lOracle.newSingle( "aop-pd", false ) );

        // by artifact
        lOracle.addLicenseForArtifact( Coordinates.valueOf( "dom4j:dom4j:1.6.1" ), lOracle.getOrThrowByName( "dom4j" ) );

        lOracle.addLicenseForArtifact( new Coordinates( "net.jcip", "jcip-annotations", "1.0" ), lOracle.getOrThrowByName( "CC-BY-2.5" ) );

        lOracle.addLicenseForArtifact( new Coordinates( "javax.servlet", "javax.servlet-api", "3.1.0" ),
                                       lOracle.getOrThrowByName( "CDDl-1.1 or GPL-2.0 with Classpath-exception-2.0" ) );
        lOracle.addLicenseForArtifact( new Coordinates( "org.apache.httpcomponents", "httpclient", "4.0.1" ),
                                       lOracle.getOrThrowByName( "Apache-2.0" ) );
        lOracle.addLicenseForArtifact( Coordinates.valueOf( "net.sourceforge.pmd:pmd-*:5.4.1" ),
                                       lOracle.getOrThrowByName( "bsd-4-clause" ) );
        lOracle.addLicenseForArtifact( Coordinates.valueOf( "org.scala-lang:scala*:2.10.0" ), // TODO verify  or via timed url ?
                                       lOracle.getOrThrowByName( "bsd-2-clause" ) );
        lOracle.addLicenseForArtifact( Coordinates.valueOf( "org.scala-lang:scala*:2.10.5" ), // TODO verify
                                       lOracle.getOrThrowByName( "bsd-2-clause" ) );
        lOracle.addLicenseForArtifact( Coordinates.valueOf( "antlr:antlr:2.7.7" ), // TODO verify, before antlr has antlr-pd
                                       lOracle.getOrThrowByName( "bsd-2-clause" ) );
        lOracle.addLicenseForArtifact( Coordinates.valueOf( "antlr:antlr:3.5.2" ), // TODO verify, before antlr has antlr-pd
                                       lOracle.getOrThrowByName( "bsd-2-clause" ) );
        lOracle.addLicenseForArtifact( Coordinates.valueOf( "org.antlr:ST4:4.0.4" ), // TODO verify, before antlr has antlr-pd
                                       lOracle.getOrThrowByName( "bsd-2-clause" ) );
        lOracle.addLicenseForArtifact( Coordinates.valueOf( "jline:jline:1.0" ),
                                       lOracle.getOrThrowByName( "bsd-2-clause" ) );
        lOracle.addLicenseForArtifact( Coordinates.valueOf( "org.tachyonproject:tachyon-*:0.8.2" ),
                                       lOracle.getOrThrowByName( "apache-2" ) );
        lOracle.addLicenseForArtifact( Coordinates.valueOf( "com.thoughtworks.paranamer:paranamer:2.3" ),
                                       lOracle.getOrThrowByName( "bsd-2-clause" ) );

        lOracle.addLicenseForArtifact( Coordinates.valueOf( "org.json4s:json4s-*:3.2.10" ),
                                       lOracle.getOrThrowByName( "apache-2" ) );

        // bsd-2-clause hmm
        //http://www.opensource.org/licenses/bsd-license.php
        //http://asm.objectweb.org/license.html

        // aduna bsd ? 3-clause

        // com.jcabi:jcabi-log:0.14       :  http://www.jcabi.com/LICENSE.txt
        // com.jcabi:jcabi-manifests:1.1

        // scala 2.10.5

        System.out.println( "----------------------- ");
        System.out.println( "------ fedora --------- ");
        System.out.println( "----------------------- ");


        new ExtractGoodFedoraLicensesFromHTML().addFedoraInfo( lOracle );
        new ExtractBadFedoraLicensesFromHTML().addFedoraInfo( lOracle );

        System.out.println( "\n\n#licenses " + lOracle.getSingleLicenseCount() + "\n\n");


        // checkat

        lOracle.addUrlCheckedAt( lOracle.getOrThrowByName( "bsd-3-clause" ), "http://asm.objectweb.org/license.html", "2016-06-08" );
        lOracle.addUrlCheckedAt( lOracle.getOrThrowByName( "bsd-3-clause" ), "http://asm.ow2.org/license.html", "2016-06-08" ); // todo
        lOracle.addUrlCheckedAt( lOracle.getOrThrowByName( "bsd-2-clause" ), "http://antlr.org/license.html", "2016-06-08" ); // todo
        lOracle.addUrlCheckedAt( lOracle.getOrThrowByName( "bsd-2-clause" ), "http://www.opensource.org/licenses/bsd-license.php", "2016-06-08" );
        lOracle.addUrlCheckedAt( lOracle.getOrThrowByName( "bsd-4-clause" ), "http://hsqldb.org/web/hsqlLicense.html", "2016-06-08" );
        lOracle.addUrlCheckedAt( lOracle.getOrThrowByName( "bsd-3-clause" ), "http://repo.aduna-software.org/legal/aduna-bsd.txt", "2016-06-08" );
        lOracle.addUrlCheckedAt( lOracle.getOrThrowByName( "bsd-4-clause" ), "http://www.jcabi.com/LICENSE.txt", "2016-06-09" );  // todo
        lOracle.addUrlCheckedAt( lOracle.getOrThrowByName( "bsd-2-clause" ), " http://antlr.org/license.html", "2016-06-09" );  // todo

        System.out.println( "----------------------- ");
        System.out.println( "------ ifross.org ----- ");
        System.out.println( "----------------------- ");

        // strong copy left
        strongCL( lOracle );

        System.out.println( "\n\n#licenses " + lOracle.getSingleLicenseCount() + "\n\n");


        Filess.write( _nn( _nn( Paths.get( "" ).toAbsolutePath() ).resolve( "src/main/resources/de/pfabulist/loracle/loracle.json" ) ),
                      getBytes( new GsonBuilder().setPrettyPrinting().create().toJson( lOracle ) ) );

        testAll( lOracle );

    }

    public void testAll( LOracle lOracle ) {
        assertThat( lOracle.getByUrl( "http://www.apache.org/licenses/LICENSE-2.0" ) ).isPresent();
        assertThat( lOracle.getByUrl( "http://www.apache.org/licenses/LICENSE-2.0.TXT" ) ).isPresent();
        assertThat( lOracle.getByUrl( "http://www.opensource.org/licenses/mit-license.php" ) ).isPresent();
        assertThat( lOracle.getByUrl( "glassfish.java.net/public/cddl+gpl_1_1" ) ).isPresent();
        assertThat( lOracle.getByCoordinates( Coordinates.valueOf( "aopalliance:aopalliance:1.0" ) ) ).isPresent();

        assertThat( lOracle.getByCoordinates( Coordinates.valueOf( "javax.servlet:javax.servlet-api:3.1.0" ) ) ).isEqualTo(
                lOracle.getByName( "CDDl-1.1 or GPL-2.0 with Classpath-exception-2.0" ) );

        assertThat( lOracle.getOrThrowByName( "MPL 2.0" ) ).isEqualTo( lOracle.getOrThrowByName( "mpl-2.0" ) );
        assertThat( lOracle.getByName( "MPL 2.0, and EPL 1.0" ) ).isPresent();

        assertThat( lOracle.getOrThrowByName( "ASL-1.1" ) ).isEqualTo( lOracle.getOrThrowByName( "apache-1.1" ) );

        assertThat( lOracle.getByUrl( "gnu.org/licenses/lgpl.html" ) ).isEqualTo( lOracle.getByName( "lgpl-3.0+" ) );

        assertThat( lOracle.getOrThrowByName( "GPL-2.0 or later" ) ).isEqualTo( lOracle.getOrThrowByName( "GPL-2.0+" ) );

        assertThat( lOracle.getByUrl( "http://opensource.org/licenses/BSD-2-Clause" ) ).
                isEqualTo( lOracle.getByName( "bsd-2-clause" ) );

        assertThat( lOracle.getByName( "Indiana University Extreme! Lab Software License, vesion 1.1.1" ) ).
                isEqualTo( lOracle.getByName( "indiana-extreme-1.1.1" ) );

        assertThat( lOracle.getByUrl( "http://freemarker.org/LICENSE.txt" ) ).isPresent();

        assertThat( lOracle.getByUrl( "http://htmlparser.sourceforge.net/cpl1.0.txt" ) ).isPresent();
        assertThat( lOracle.getByUrl( "http://foo.bar/baz/lgpl-2.1.html" ) ).isPresent();
        assertThat( lOracle.getByUrl( "gnu.org/licenses/lgpl-2.1" ) ).isPresent();
        assertThat( lOracle.getByUrl( "repository.jboss.org/licenses/cc0-1.0" ) ).isPresent();
        assertThat( lOracle.getByUrl( "www.fsf.org/licensing/licenses/agpl-3.0.html" ) ).isPresent();

        assertThat( lOracle.getByUrl( "http://creativecommons.org/publicdomain/zero/1.0/" ) ).
                isEqualTo( lOracle.getByName( "cc0-1.0" ) );

        assertThat( lOracle.getByUrl( "http://www.opensource.org/licenses/cpl1.0" ) ).
                isEqualTo( lOracle.getByName( "cpl-1.0" ) );

        assertThat( lOracle.getByCoordinates( Coordinates.valueOf( "org.scala-lang:scalap:2.10.0" ) ) ).isPresent();

        lOracle.allowUrlsCheckedDaysBefore( 100 );
//        System.out.println( lOracle.guessByUrl( "http://www.opensource.org/licenses/bsd-license.php" ) );
        assertThat( lOracle.getByUrl( "http://www.opensource.org/licenses/bsd-license.php" ) ).isPresent();

    }

    public void strongCL( LOracle lOracle ) {
        // http://www.ifross.org/ifross_html/lizenzcenter-en.html

//        LOracle lOracle = JSONStartup.start().spread();

        lOracle.getMore( lOracle.getOrThrowByName( "gpl-2.0" ) ).copyLeft = true;
        lOracle.getMore( lOracle.getOrThrowByName( "gpl-2.0+" ) ).copyLeft = true;
        lOracle.getMore( lOracle.getOrThrowByName( "gpl-3.0" ) ).copyLeft = true;
        lOracle.getMore( lOracle.getOrThrowByName( "gpl-3.0+" ) ).copyLeft = true;
        lOracle.getMore( lOracle.getOrThrowByName( "agpl-1.0" ) ).copyLeft = true;
        lOracle.getMore( lOracle.getOrThrowByName( "agpl-1.0+" ) ).copyLeft = true;
        lOracle.getMore( lOracle.getOrThrowByName( "agpl-3.0" ) ).copyLeft = true;
        lOracle.getMore( lOracle.getOrThrowByName( "agpl-3.0+" ) ).copyLeft = true;

        lOracle.getMore( lOracle.getOrThrowByName( "agpl-1.0" ) ).copyLeft = true;
        //System.out.println( lOracle.getOrThrowByName( "Affero General Public License" ));
        //System.out.println( lOracle.getByUrl( "http://www.affero.org/oagpl.html" ));

        {
            LicenseID alt = lOracle.newSingle( "alternate-1.1", false );
            lOracle.getMore( alt ).copyLeft = true;
            lOracle.addLongName( alt, "Alternate Route Open Source License (v. 1.1)" );
            lOracle.addUrl( alt, "http://www.wsdot.wa.gov/eesc/bridge/alternateroute/arosl.htm" );
        }
        System.out.println( lOracle.getByName( "Alternate Route Open Source License (v. 1.1)" ) );
        System.out.println( lOracle.getByUrl( "http://www.wsdot.wa.gov/eesc/bridge/alternateroute/arosl.htm" ) );
        System.out.println("");

        lOracle.getMore( lOracle.getOrThrowByName( "cecill-2.0" ) ).copyLeft = true;
        System.out.println( lOracle.getByName( "CeCILL License (v. 2)" ) );
        System.out.println( lOracle.getByUrl( "http://www.cecill.info/licences/Licence_CeCILL_V2-en.txt" ) );
        System.out.println("");

        {
            LicenseID cp = lOracle.newSingle( "crosspoint-1.1", false );
            lOracle.getMore( cp ).copyLeft = true;
            lOracle.addLongName( cp, "CrossPoint Source Code License, (only in German)" );
            lOracle.addLongName( cp, "CrossPoint Source Code License" );
            lOracle.addUrl( cp, "http://www.crosspoint.de/srclicense.html" );
        }
        System.out.println( lOracle.getByName( "CrossPoint Source Code License, (only in German)" ) );
        System.out.println( lOracle.getByUrl( "http://www.crosspoint.de/srclicense.html" ) );
        System.out.println("");
//
        System.out.println( lOracle.getByName( "eCos License (v. 2.0)" ) );
        System.out.println( lOracle.getByUrl( "http://www.gnu.org/licenses/ecos-license.html" ) );
        System.out.println("");
//
        System.out.println( lOracle.getByName( "FreeCard License" ) );
        System.out.println( lOracle.getByUrl( "http://freecard.sourceforge.net/website/licence/license.php" ) );
        System.out.println("");
//
        System.out.println( lOracle.getByName( "GNU Classpath -GPL with special exception " ) );
        System.out.println( lOracle.getByUrl( "http://www.gnu.org/software/classpath/license.html" ) );
        System.out.println("");
//
        System.out.println( lOracle.getByName( "GNU Emacs General Public License" ) );
        System.out.println( lOracle.getByUrl( "http://www.free-soft.org/gpl_history/emacs_gpl.html" ) );
        System.out.println("");
//
        System.out.println( lOracle.getByName( "GNU General Public License (GPL) (v. 1.0)" ) );
        System.out.println( lOracle.getByUrl( "http://www.gnu.org/copyleft/copying-1.0.html" ) );
        System.out.println("");
//
        System.out.println( lOracle.getByName( "GNU General Public License (GPL) (v. 2.0)" ) );
        System.out.println( lOracle.getByUrl( "http://www.gnu.org/licenses/old-licenses/gpl-2.0.html" ) );
        System.out.println("");
//
        System.out.println( lOracle.getByName( "GNU General Public License (GPL) (v. 3.0)" ) );
        System.out.println( lOracle.getByUrl( "http://www.fsf.org/licensing/licenses/gpl.html" ) );
        System.out.println("");
//
        System.out.println( lOracle.getByName( "GNU General Public License (GPL) (v. 3.0) - inofficial German translation" ) );
        System.out.println( lOracle.getByUrl( "http://www.gnu.de/gpl-ger.html" ) );
        System.out.println("");
//
        System.out.println( lOracle.getByName( "GNU Affero General Public License (GPL) (v. 3.0)" ) );
        System.out.println( lOracle.getByUrl( "http://www.fsf.org/licensing/licenses/agpl-3.0.html" ) );
        System.out.println("");
//
        {
            LicenseID li = lOracle.newSingle( "hpl-1.0", false );
            lOracle.getMore( li ).copyLeft = true;
            lOracle.addLongName( li, "Honest Public License (HPL) (1.0)" );
            lOracle.addLongName( li, "Honest Public License (1.0)" );
            lOracle.addUrl( li, "http://www.projectpier.org/manual/tour/licence" );

        }
        System.out.println( lOracle.getByName( "Honest Public License (HPL) (1.0)" ) );
        System.out.println( lOracle.getByUrl( "http://www.projectpier.org/manual/tour/licence" ) );
        System.out.println("");
//
        {
            LicenseID li = lOracle.newSingle( "hpl-1.1", false );
            lOracle.getMore( li ).copyLeft = true;
            lOracle.addLongName( li, "Honest Public License (HPL) (1.1)" );
            lOracle.addLongName( li, "Honest Public License (1.1)" );
            lOracle.addUrl( li, "http://www.opensourcestrategies.com/HPLv1.1.txt" );

        }
        System.out.println( lOracle.getByName( "Honest Public License (HPL) (1.1)" ) );
        System.out.println( lOracle.getByUrl( "http://www.opensourcestrategies.com/HPLv1.1.txt" ) );
        System.out.println("");
//
        System.out.println( lOracle.getByName( "Open RTLinux Patent License" ) );
        System.out.println( lOracle.getByUrl( "http://www.rtlinuxfree.com/openpatentlicense.html" ) );
        System.out.println("");
//
//
        lOracle.getMore( lOracle.getOrThrowByName( "apsl-2.0" ) ).copyLeft = true;
        System.out.println( lOracle.getByName( "Apple Public Source License (v. 2.0)" ) );
        System.out.println( lOracle.getByUrl( "http://www.opensource.apple.com/apsl/2.0.txt" ) );
        System.out.println("");
//
        System.out.println( lOracle.getByName( "Arphic Public License" ) );
        System.out.println( lOracle.getByUrl( "http://ftp.gnu.org/non-gnu/chinese-fonts-truetype/LICENSE" ) );
        System.out.println("");
//
        System.out.println( lOracle.getByName( "Boost Software License" ) );
        System.out.println( lOracle.getByUrl( "http://www.boost.org/LICENSE_1_0.txt" ) );
        System.out.println("");
//
        lOracle.getMore( lOracle.getOrThrowByName( "cpl-1.0" ) ).copyLeft = true;
        System.out.println( lOracle.getByName( "Common Public License" ) );
        System.out.println( lOracle.getByUrl( "http://www.eclipse.org/legal/cpl-v10.html" ));
        System.out.println("");
//
        lOracle.getMore( lOracle.getOrThrowByName( "d-fsl-1.0-en" ) ).copyLeft = true;
        lOracle.addLongName( lOracle.getOrThrowByName( "d-fsl-1.0-en" ), "Deutsche Freie Softwarelizenz (d-fsl) - (German Free Software License)" );
        lOracle.addLongName( lOracle.getOrThrowByName( "d-fsl-1.0-en" ), "Deutsche Freie Softwarelizenz" );
        lOracle.addLongName( lOracle.getOrThrowByName( "d-fsl-1.0-en" ), "German Free Software License" );
        System.out.println( lOracle.getByName( "Deutsche Freie Softwarelizenz (d-fsl) - (German Free Software License)" ) );
        System.out.println( lOracle.getByUrl( "http://www.dipp.nrw.de/d-fsl/index_html/lizenzen/en/D-FSL-1_0_en.txt" ));
        System.out.println("");
//
        lOracle.getMore( lOracle.getOrThrowByName( "epl-1.0" ) ).copyLeft = true;
        System.out.println( lOracle.getByName( "Eclipse Public License (v. 1.0)" ) );
        System.out.println( lOracle.getByUrl( "http://www.eclipse.org/legal/epl-v10.html" ) );
        System.out.println("");
//
        // eupl-1.0 ?
        System.out.println( lOracle.getByName( "European Public License (v. 1.0)" ) );
        System.out.println( lOracle.getByUrl( "http://ec.europa.eu/idabc/en/document/6523" ) );
        System.out.println("");
//
        // ipl-1.0 ?
        System.out.println( lOracle.getByName( "IBM Public License" ) );
        System.out.println( lOracle.getByUrl( "http://www-128.ibm.com/developerworks/opensource/library/os-i18n2/os-ipl.html" ) );
        System.out.println("");
//
        System.out.println( lOracle.getByName( "Jabber Open Source License" ) );
        System.out.println( lOracle.getByUrl( "http://www.jabber.org/about/josl.shtml" ) );
        System.out.println("");
//
        lOracle.getMore( lOracle.getOrThrowByName( "lpl-1.02" ) ).copyLeft = true;
        lOracle.addUrl( lOracle.getOrThrowByName( "lpl-1.02" ), "http://plan9.bell-labs.com/plan9/license.html" );
//        System.out.println( lOracle.getByName( "Lucent Public License Version (v. 1.02)" ) );
//        System.out.println( lOracle.getByUrl( "http://plan9.bell-labs.com/plan9/license.html" ) );
//        System.out.println("");
//
        lOracle.getMore( lOracle.getOrThrowByName( "ngpl" ) ).copyLeft = true;
//        System.out.println( lOracle.getByName( "Nethack General Public License" ) );
//        System.out.println( lOracle.getByUrl( "http://www.nethack.org/common/license.html" ) );
//        System.out.println("");
//
        lOracle.getMore( lOracle.getOrThrowByName( "open-group" ) ).copyLeft = true;
        lOracle.addUrl( lOracle.getOrThrowByName( "open-group" ), "http://www.opengroup.org/openmotif/license" );
//        System.out.println( lOracle.getByName( "Open Group Public License" ) );
//        System.out.println( lOracle.getByUrl( "http://www.opengroup.org/openmotif/license" ) );
//        System.out.println("");
//
        lOracle.getMore( lOracle.getOrThrowByName( "osl-2.1" ) ).copyLeft = true;
//        System.out.println( lOracle.getByName( "Open Software License (OSL) (v 2.1)" ) );
//        System.out.println( lOracle.getByUrl( "http://opensource.org/licenses/osl-2.1.php" ) );
//        System.out.println("");
//
        System.out.println( lOracle.getByName( "RedHat eCos Public License (v. 1.1)" ) );
        System.out.println( lOracle.getByUrl( "http://ecos.sourceware.org/old-license.html" ) );
        System.out.println("");
//
        System.out.println( lOracle.getByName( "RedHat eCos Public License (v. 1.1)" ) );
        System.out.println( lOracle.getByUrl( "http://sources.redhat.com/ecos/license-overview.html" ) );
        System.out.println("");
//
        System.out.println( lOracle.getByName( "Salutation Public License" ) );
        System.out.println( lOracle.getByUrl( "http://web.archive.org/web/20050323201906/http://www.salutation.org/lite/lite_license.htm" ) );
        System.out.println("");
//
        System.out.println( lOracle.getByName( "Software AG License Terms (Quip License) (v. 1.3)" ) );
        System.out.println( lOracle.getByUrl( "http://www.cse.uconn.edu/~dqg/cse350/xml/quip/License.txt" ) );
        System.out.println("");
//
        lOracle.getMore( lOracle.getOrThrowByName( "vim" ) ).copyLeft = true;
        lOracle.addUrl( lOracle.getOrThrowByName( "vim" ), "http://www.vim.org/htmldoc/uganda.html" );
//        System.out.println( lOracle.getByName( "Vim License" ) );
//        System.out.println( lOracle.getByUrl( "http://www.vim.org/htmldoc/uganda.html" ) );
//        System.out.println("");

    }

    @Test
    public void startup() {
        LOracle lOracle = JSONStartup.start().spread();

        testAll( lOracle );
    }

    @Test
    public void testJson() {
        new ExtractSpdxLicensesFromJSON().go( new LOracle() );
    }

//    @Test
//    public void fedora() {
//        LOracle lOracle = new LOracle( new ExtractSpdxLicensesFromHTML().getLicenses() );
//
//        new ExtractGoodFedoraLicensesFromHTML().getFedoraInfo().peek( System.out::println ).
//                map( f -> lOracle.getByName( f.name ) ).
//                forEach( System.out::println );
//    }
//
//    @Test
//    public void gson() {
//        List<IANALicense> ll = new ExtractSpdxLicensesFromHTML().getLicenses();
//
//        System.out.println( new GsonBuilder().setPrettyPrinting().create().toJson( ll ));
//    }
//
//    @Test
//    public void upperCaseTest() {
//        LOracle lOracle = new LOracle( new ExtractSpdxLicensesFromHTML().getLicenses() );
//
//        assertThat( lOracle.getByName( "BSD-2-Clause" )).isPresent();
//    }
}