//
// $Id$

package com.samskivert.condep

import sbt._

/**
 * Allows projects to be symlinked into the current directory for a direct dependency, or fall back
 * to obtaining the project from Maven otherwise.
 */
class Depends (deps :(String, String, ModuleID)*)
{
  /**
   * Adds direct dependencies to the supplied project for all dependencies that could be resolved
   * via their local symlink.
   */
  def addDeps (p :Project) = (deps collect {
    case (id, subp, dep) if (file(id).exists) => symproj(file(id), subp)
  }).foldLeft(p) { _ dependsOn _ }

  /**
   * Returns a list of all dependencies that could not be resolved via their local symlink.
   */
  def libDeps = deps collect {
    case (id, subp, dep) if (!file(id).exists) => dep
  }

  private def symproj (dir :File, subproj :String = null) =
    if (subproj == null) RootProject(dir) else ProjectRef(dir, subproj)
}

object Depends
{
  def apply (deps :(String, String, ModuleID)*) = new Depends(deps :_*)
}
