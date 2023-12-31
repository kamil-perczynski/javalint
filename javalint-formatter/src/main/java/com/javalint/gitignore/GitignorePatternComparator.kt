package com.javalint.gitignore

class GitignorePatternComparator : Comparator<GitignorePattern> {

  companion object {

    val INSTANCE = GitignorePatternComparator()
  }

  override fun compare(a: GitignorePattern, b: GitignorePattern): Int {
    if (a.type == b.type) {
      return 0
    }
    if (a.type == GitignorePattern.Type.INCLUDES) {
      return -1
    }
    return 1
  }

}
