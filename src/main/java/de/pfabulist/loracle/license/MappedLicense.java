package de.pfabulist.loracle.license;

import de.pfabulist.roast.functiontypes.Consumerr;
import de.pfabulist.roast.functiontypes.Supplierr;
import de.pfabulist.roast.unchecked.Unchecked;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static de.pfabulist.roast.NonnullCheck._nn;

/**
 * Copyright (c) 2006 - 2016, Stephan Pfab
 * SPDX-License-Identifier: BSD-2-Clause
 */

public class MappedLicense {
    private final
    @Nullable
    LicenseID license;
    private final String reason;
    private final List<MappedLicense> diffOver = new ArrayList<>();

    private final static MappedLicense empty = new MappedLicense();

    private MappedLicense() {
        license = null;
        reason = "";
    }

    private MappedLicense( LicenseID licenseID, String reason ) {
        this.license = licenseID;
        this.reason = reason;

        if( reason.isEmpty() ) {
            throw new IllegalArgumentException( "can't have a MappedLicense without reason" );
        }
    }

    public MappedLicense( String reason ) {
        this.license = null;
        this.reason = reason;
        if( reason.isEmpty() ) {
            throw new IllegalArgumentException( "can't have a MappedLicense without reason" );
        }
    }

    public static MappedLicense empty() {
        return empty;
    }

    public static MappedLicense of( LicenseID licenseID, String reason ) {
        return new MappedLicense( licenseID, reason );
    }

    public static MappedLicense of( Optional<LicenseID> licenseID, String reason ) {
        return licenseID.map( l -> of( l, reason ) ).orElse( empty() );
    }

    public boolean isPresent() {
        return license != null;
    }

    public LicenseID orElseThrow( Supplierr<Exception> ex ) {
        if( !isPresent() ) {
            throw Unchecked.u( ex.get() );
        }

        return _nn( license );
    }

    public void ifPresent( Consumerr<LicenseID> con ) {
        if( isPresent() ) {
            con.accept( _nn( license ));
        }
    }

    public MappedLicense addReason( String more ) {
        if( !isPresent() ) {
            return MappedLicense.empty();
        }
        if( more.isEmpty() ) {
            return this;
        }
        return new MappedLicense( _nn( license ), reason + " && " + more );
    }

    @Override
    public String toString() {
        if( isPresent() ) {
            String diff = "";
            if ( !diffOver.isEmpty()) {
                diff = "!override! " + " [" + diffOver.stream().map( Object::toString ).collect( Collectors.joining() ) + "]";
            }
            return _nn( license ) + " [" + reason + "]" + diff;
        }

        return "-";
    }

    public String getReason() {
        return reason;
    }

    public Optional<LicenseID> noReason() {
        return Optional.ofNullable( license );
    }

    @Override
    @SuppressFBWarnings( "NP_METHOD_PARAMETER_TIGHTENS_ANNOTATION" )
    public boolean equals( @Nullable Object o ) {
        if( this == o ) {
            return true;
        }
        if( o == null || getClass() != o.getClass() ) {
            return false;
        }

        MappedLicense that = (MappedLicense) o;

        return license != null ? license.equals( that.license ) : that.license == null;

    }

    @Override
    public int hashCode() {
        return license != null ? license.hashCode() : 0;
    }

    public <U> U orElse( Function<LicenseID, U> f, U els ) {
        if( isPresent() ) {
            return _nn( f.apply( _nn( license ) ) );
        }

        return els;
    }

    public static MappedLicense empty( String s ) {
        return new MappedLicense( s );
    }

    public void addOver( MappedLicense other ) {

        if ( this == other ) {
            return;
        }

        if( other.isPresent() && !equals( other ) ) {
            diffOver.add( other );
        }

        other.diffOver.forEach( over -> {
            if ( !equals( over )) {
                diffOver.add( over );
            }
        } );
    }

    public void addOverFrom( MappedLicense other ) {
        other.diffOver.forEach( over -> {
            if ( equals( over )) {
                diffOver.add( over );
            }
        } );
    }

    public static MappedLicense decide( MappedLicense... mls ) {
        MappedLicense ret = Arrays.stream( mls )
                .filter( MappedLicense::isPresent )
                .findFirst()
                .orElse( MappedLicense.empty() );

        Arrays.stream( mls ).forEach( ret::addOver );

        return ret;
    }


}
