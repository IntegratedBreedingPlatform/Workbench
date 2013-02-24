# This installs the following:
#   - workbench
#   - workbench tools,
#   - start menu shortcuts
#   - an uninstaller, and
#   - uninstall information to the registry for Add/Remove Programs

# To create the installer, run the build-installer.xml Ant script,
# and run makensis on the script on the dist directory.

# If you change the names "app.exe", "logo.ico", or "license.rtf" you should do a search and replace - they
# show up in a few places.
# All the other settings can be tweaked by editing the !defines at the top of this script
!define APPNAME "IBPWorkbench"
!define COMPANYNAME "Efficio"
!define DESCRIPTION "IBPWorkbench installation including workbench tools."

# These three must be integers
!define VERSIONMAJOR 1
!define VERSIONMINOR 0
!define VERSIONBUILD 0

# These will be displayed by the "Click here for support information" link in "Add/Remove Programs"
# It is possible to use "mailto:" links in here to open the email client
!define HELPURL     "http://..." # "Support Information" link
!define UPDATEURL   "http://..." # "Product Updates" link
!define ABOUTURL    "http://..." # "Publisher" link

# This is the size (in kB) of all the files copied into "Program Files"
!define INSTALLSIZE 7233
 
RequestExecutionLevel admin ;Require admin rights on NT6+ (When UAC is turned on)
 
#InstallDir "$PROGRAMFILES\${COMPANYNAME}\${APPNAME}"
InstallDir "${COMPANYNAME}\${APPNAME}"
 
# rtf or txt file - remember if it is txt, it must be in the DOS text format (\r\n)
LicenseData "license.rtf"

# This will be in the installer/uninstaller's title bar
Name "${APPNAME}"
Icon "logo.ico"
OutFile "ibpworkbench-win32.exe"
 
!include LogicLib.nsh
 
# Just three pages - license agreement, install location, and installation
Page license
Page directory
Page instfiles
 
!macro VerifyUserIsAdmin
UserInfo::GetAccountType
Pop $0
${If} $0 != "admin" ;Require admin rights on NT4+
        MessageBox mb_iconstop "Administrator rights required!"
        SetErrorLevel 740 ;ERROR_ELEVATION_REQUIRED
        Quit
${EndIf}
!macroend
 
Function .onInit
    SetShellVarContext all
    !insertmacro VerifyUserIsAdmin
FunctionEnd
 
Section "install"
    # Files for the install directory - to build the installer, these should be in the same directory as the install script (this file)
    SetOutPath $INSTDIR
    
    # Files added here should be removed by the uninstaller (see section "uninstall")
    File "ibpworkbench.exe"
    File "logo.ico"
    File "ibpworkbench_launcher.jar"
    File /r "images"
    File /r "jre"
    File /r "mysql"
    File /r "tomcat"
    
    # Add any other files for the install directory (license files, app data, etc) here
 
    # Uninstaller - See function un.onInit and section "uninstall" for configuration
    WriteUninstaller "$INSTDIR\uninstall.exe"
 
    # Start Menu
    CreateDirectory "$SMPROGRAMS\${COMPANYNAME}"
    CreateShortCut "$SMPROGRAMS\${COMPANYNAME}\${APPNAME}.lnk" "$INSTDIR\app.exe" "" "$INSTDIR\logo.ico"
 
    # Registry information for add/remove programs
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${COMPANYNAME} ${APPNAME}" "DisplayName" "${COMPANYNAME} - ${APPNAME} - ${DESCRIPTION}"
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${COMPANYNAME} ${APPNAME}" "UninstallString" "$\"$INSTDIR\uninstall.exe$\""
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${COMPANYNAME} ${APPNAME}" "QuietUninstallString" "$\"$INSTDIR\uninstall.exe$\" /S"
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${COMPANYNAME} ${APPNAME}" "InstallLocation" "$\"$INSTDIR$\""
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${COMPANYNAME} ${APPNAME}" "DisplayIcon" "$\"$INSTDIR\logo.ico$\""
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${COMPANYNAME} ${APPNAME}" "Publisher" "$\"${COMPANYNAME}$\""
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${COMPANYNAME} ${APPNAME}" "HelpLink" "$\"${HELPURL}$\""
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${COMPANYNAME} ${APPNAME}" "URLUpdateInfo" "$\"${UPDATEURL}$\""
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${COMPANYNAME} ${APPNAME}" "URLInfoAbout" "$\"${ABOUTURL}$\""
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${COMPANYNAME} ${APPNAME}" "DisplayVersion" "$\"${VERSIONMAJOR}.${VERSIONMINOR}.${VERSIONBUILD}$\""
    WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${COMPANYNAME} ${APPNAME}" "VersionMajor" ${VERSIONMAJOR}
    WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${COMPANYNAME} ${APPNAME}" "VersionMinor" ${VERSIONMINOR}
    # There is no option for modifying or repairing the install
    WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${COMPANYNAME} ${APPNAME}" "NoModify" 1
    WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${COMPANYNAME} ${APPNAME}" "NoRepair" 1
    # Set the INSTALLSIZE constant (!defined at the top of this script) so Add/Remove Programs can accurately report the size
    WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${COMPANYNAME} ${APPNAME}" "EstimatedSize" ${INSTALLSIZE}
SectionEnd
 
# Uninstaller
 
Function un.onInit
    SetShellVarContext all
 
    #Verify the uninstaller - last chance to back out
    MessageBox MB_OKCANCEL "Permanantly remove ${APPNAME}?" IDOK next
        Abort
    next:
    !insertmacro VerifyUserIsAdmin
FunctionEnd
 
Section "uninstall"
 
    # Remove Start Menu launcher
    delete "$SMPROGRAMS\${COMPANYNAME}\${APPNAME}.lnk"
    # Try to remove the Start Menu folder - this will only happen if it is empty
    rmDir "$SMPROGRAMS\${COMPANYNAME}"
 
    # Remove files
    Delete "ibpworkbench.exe"
    Delete "logo.ico"
    Delete "ibpworkbench_launcher.jar"
    Delete "images"
    Delete "jre"
    Delete "mysql"
    Delete "tomcat"
 
    # Always delete uninstaller as the last action
    delete $INSTDIR\uninstall.exe
 
    # Try to remove the install directory - this will only happen if it is empty
    rmDir $INSTDIR
 
    # Remove uninstaller information from the registry
    DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${COMPANYNAME} ${APPNAME}"
SectionEnd