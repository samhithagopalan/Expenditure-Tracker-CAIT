import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  profile: any = {};

  editMode = false;

  editName = '';
  editPassword = '';
  editPictureMode = false;
  successMessage = '';
  selectedFile: File | null = null;
  constructor(
    private apiService: ApiService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {

    const userId =
      this.authService.currentUserValue?.id;

    this.apiService.getProfileSummary(userId)
      .subscribe(data => {

        this.profile = data;

        this.editName = data.name;
      });
  }

  startEdit(): void {
    this.editMode = true;
  }

  cancelEdit(): void {

    this.editMode = false;

    this.editName = this.profile.name;
    this.editPassword = '';
  }

  saveProfile(): void {

    const payload = {
      name: this.editName,
      password:
        this.editPassword
          ? this.editPassword
          : null
    };

    this.apiService.updateProfile(
      this.profile.id,
      payload
    ).subscribe(() => {

      this.successMessage =
        'Profile updated successfully';

      this.editMode = false;

      this.loadProfile();
    });
  }
  onFileSelected(event: any): void {

    if (event.target.files.length > 0) {

      this.selectedFile =
        event.target.files[0];
    }
  }

  uploadPicture(): void {

    if (!this.selectedFile) {
      return;
    }

    this.apiService.uploadProfilePicture(
      this.profile.id,
      this.selectedFile
    ).subscribe(() => {

      this.successMessage =
        'Profile picture uploaded successfully';

      this.loadProfile();
      this.editPictureMode = false;
    });
  }
  startPictureEdit(): void {
    this.editPictureMode = true;
  }

  cancelPictureEdit(): void {
    this.editPictureMode = false;
    this.selectedFile = null;
  }
  deletePicture(): void {

    this.apiService
        .deleteProfilePicture(
            this.profile.id)
        .subscribe(() => {

            this.loadProfile();
        });
  }
}