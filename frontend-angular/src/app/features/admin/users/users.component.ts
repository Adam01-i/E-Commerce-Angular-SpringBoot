import { Component, OnInit } from '@angular/core';
import { User } from '../../../core/models/models';
import { UserAdminService } from '../../../core/services/user-admin.service';
import { AdminNavComponent } from '../admin-nav/admin-nav.component';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [AdminNavComponent],
  templateUrl: './users.component.html'
})
export class AdminUsersComponent implements OnInit {
  users: User[] = [];
  loading = true;

  constructor(private userAdminService: UserAdminService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.userAdminService.list().subscribe((users) => {
      this.users = users;
      this.loading = false;
    });
  }

  toggleActive(user: User): void {
    const request$ = user.isActive
      ? this.userAdminService.deactivate(user.id)
      : this.userAdminService.activate(user.id);
    request$.subscribe(() => this.load());
  }
}
