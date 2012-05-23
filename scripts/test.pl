#!/usr/local/bin/perl

use CAIDA::Countries;
    my $countries = new CAIDA::Countries();
    print "Canada's 2-letter ISO code is: " .
        $country->get_iso2code_by_name('Canada') . "\n";
    print "Zimbabwe is on the continent of " .
        $country->get_contname_by_contcode(
        $country->get_continent_by_iso2code(
        $country->get_iso2code_by_name('Zimbabwe'))) . ".\n";