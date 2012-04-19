/*
 *  NuagesPompe.scala
 *  (NuagesPompe)
 *
 *  Copyright (c) 2010-2012 Hanns Holger Rutz. All rights reserved.
 *
 *  This software is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either
 *  version 2, june 1991 of the License, or (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License (gpl.txt) along with this software; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package de.sciss.nuages

object NuagesPompe {
  val name          = "NuagesPompe"
  val version       = 0.34
  val copyright     = "(C)opyright 2010-2012 Hanns Holger Rutz"
  val isSnapshot    = false

  def versionString = {
     val s = (version + 0.001).toString.substring( 0, 4 )
     if( isSnapshot ) s + "-SNAPSHOT" else s
  }

  def main( args: Array[ String ]) {
//    if( args == Array( "--test" )) {
//
//    } else {
      printInfo()
      sys.exit( 1 )
//    }
  }

  def printInfo() {
     println( "\n" + name + " v" + versionString + "\n" + copyright + ". All rights reserved.\n" +
        "This is a library which cannot be executed directly.\n" )
  }
}